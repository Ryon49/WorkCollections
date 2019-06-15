#include "shell_util.h"
#include "linkedList.h"
#include "helpers.h"

// Library Includes
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <fcntl.h>
#include <string.h>
#include <ctype.h>
#include <sys/wait.h>

void signal_handler(int);
void handle_IO_redirection(char *[], size_t);
int countNumOfPrograms(char *[], size_t);
void buildCommands(char *, char **, const size_t);

const int READ_END = 0;
const int WRITE_END = 1;

List_t bg_list;

int conditional_flag = 0;

int main(int argc, char *argv[])
{
	char *args[MAX_TOKENS + 1];
	int exec_result;
	int exit_status;
	pid_t pid;
	pid_t wait_result;

    //Initialize the linked list
    bg_list.head = NULL;
    bg_list.length = 0;
    bg_list.comparator = &timeComparator;  // Don't forget to initialize this to your comparator!!!

	// Setup segmentation fault handler
	if(signal(SIGSEGV, sigsegv_handler) == SIG_ERR)
	{
		perror("Failed to set signal handler");
		exit(-1);
	}
	if(signal(SIGCHLD, signal_handler) == SIG_ERR)
	{
		perror("Failed to set signal handler");
		exit(-1);
	}
	if(signal(SIGUSR1, signal_handler) == SIG_ERR)
	{
		perror("Failed to set signal handler");
		exit(-1);
	}

	while(1) {
		// DO NOT MODIFY buffer
		// The buffer is dynamically allocated, we need to free it at the end of the loop
		char * const buffer = NULL;
		size_t buf_size = 0;

		// Print the shell prompt
		display_shell_prompt();
		
		// Read line from STDIN
		ssize_t nbytes = getline((char **)&buffer, &buf_size, stdin);

		// No more input from STDIN, free buffer and terminate
		if(nbytes == -1) {
			free(buffer);
			break;
		}

		// Remove newline character from buffer, if it's there
		if(buffer[nbytes - 1] == '\n')
			buffer[nbytes- 1] = '\0';

		// Handling empty strings
		if(strcmp(buffer, "") == 0) {
			free(buffer);
			continue;
		}

		char *cmd = strdup(buffer);

		// Parsing input string into a sequence of tokens
		size_t numTokens;
		*args = NULL;
		numTokens = tokenizer(buffer, args);

		// general error pipe checking
		if (strcmp(args[numTokens - 1], "|") == 0) {
			fprintf(stderr, PIPE_ERR);
			continue;
		}

		if(strcmp(args[0],"exit") == 0) {
			// Terminating the shell
			while (bg_list.head != NULL) {
				ProcessEntry_t *entry = (ProcessEntry_t *)(bg_list.head->value);
				removeFront(&bg_list);

				kill(entry->pid, SIGKILL);
				printf(BG_TERM, entry->pid, entry->cmd);
			}

			free(buffer);
			return 0;
		}
		if (conditional_flag == 1) {
			node_t *node = bg_list.head;
			while (node != NULL) {
				ProcessEntry_t *entry = (ProcessEntry_t *)(node->value);
				if (waitpid(entry->pid, 0, WNOHANG) > 0) {
					printf(BG_TERM, entry->pid, entry->cmd);
					removeByPid(&bg_list, entry->pid);
				}
				node = node->next;
			}
			conditional_flag = 0;
		}

		if (strcmp(args[0], "cd") == 0) {
			char *targetDir = numTokens >= 2 ? args[1] : getenv("HOME");
			exec_result = chdir(targetDir);

			if(exec_result == -1) {
				fprintf(stderr, DIR_ERR);
			} else {
				char pwd[1024];
				printf("%s\n", getcwd(pwd, sizeof(pwd)));
			}
		} else if (strcmp(args[0], "estatus") == 0) {
			printf("%d\n", exit_status);
		} else if (strcmp(args[0], "fg") == 0) {
			pid_t pid = -1;
			if (numTokens > 1) {
				if (isNumber(args[1]) == 0) {
					printf("-1\n");
				} else {
					pid_t pid = atoi(args[1]);
					if (removeByPid(&bg_list, pid) == -1) {
						fprintf(stderr, PID_ERR);
					} else {
						wait_result = waitpid(pid, &exit_status, 0);
						if(wait_result == -1){
							printf(WAIT_ERR);
							exit(EXIT_FAILURE);
						}
						if (exit_status > 0) {
							exit_status = 1;
						}
					}
				}
			} else {
				if (bg_list.head != NULL) {
					ProcessEntry_t * entry = (ProcessEntry_t *)(bg_list.head->value);
					wait_result = waitpid(pid, &exit_status, 0);
					if(wait_result == -1){
						printf(WAIT_ERR);
						exit(EXIT_FAILURE);
					}
					if (exit_status > 0) {
						exit_status = 1;
					}
				}
			}
		} else {
			int do_bg = 0;
			if (strcmp(args[numTokens - 1], "&") == 0) {
				do_bg = 1;
			}

			// compute how many programs are there, 
			// numOfPrograms = pipe count('|') + 1;
			int numOfPrograms = countNumOfPrograms(args, numTokens);
			char **commands = malloc(sizeof(char *) * numOfPrograms);
			
			buildCommands(strdup(cmd), commands, numOfPrograms);

			int pipefd[2];
			int out = dup(STDOUT_FILENO);

			int i = 0;

			// cat simple | grep not | grep find
			// grep find <- grep not <- cat simple

			int last_pipe_pid = -1;

			for (i = numOfPrograms - 1; i >= 0; i--) {
				if (pipe(pipefd) == -1) {
					fprintf(stderr, PIPE_ERR);
					exit(EXIT_FAILURE);
				}

				pid_t pid = fork();

				if (pid == -1) {
					fprintf(stderr, "Error creating process\n");
				} else if (pid == 0) {
					numTokens = tokenizer(strdup(commands[i]), args);

					if (i > 0) {
						dup2(pipefd[READ_END], STDIN_FILENO);
					}
					close(pipefd[WRITE_END]);
					dup2(out, STDOUT_FILENO);

					handle_IO_redirection(args, numTokens);
					exec_result = execvp(args[0], &args[0]);
					if(exec_result == -1){ //Error checking
						printf(EXEC_ERR, args[0]);
						exit(EXIT_FAILURE);
					}
					exit(EXIT_SUCCESS);
				} else {
					close(out);
					close(pipefd[READ_END]);
					out = pipefd[WRITE_END];

					// remember the pid of last program
					if (i == numOfPrograms - 1) {
						last_pipe_pid = pid;
					}
				}
			}
			close(pipefd[READ_END]);

			if (do_bg) {
				ProcessEntry_t *entry = malloc(sizeof(ProcessEntry_t));
				entry->cmd = cmd;
				entry->pid = last_pipe_pid;
				entry->seconds = time(NULL);	
								
				insertInOrder(&bg_list, entry);
			} else {
				wait_result = waitpid(last_pipe_pid, &exit_status, 0);

				if(wait_result == -1){
					printf(WAIT_ERR);
					exit(EXIT_FAILURE);
				}
				if (exit_status > 0) {
					exit_status = 1;
				}
			}
		}
		
		// Free the buffer allocated from getline
		free(buffer);
	}
	return 0;
}


void handle_IO_redirection(char *args[], size_t numTokens) {
	int in = 0, out = 0, err = 0;
	char *in_file = NULL, *out_file = NULL, *err_file = NULL;

	int j = 0, ioError = 0;
	while (j < numTokens) {
		int found = 0;
		if (strcmp(args[j], "<") == 0) {
			// last without filename or duplicated
			if (j + 1 == numTokens || in > 0) {
				ioError = 1;
				break;
			}
			in = 1;
			in_file = strdup(args[j + 1]);
			found = 1;
		} else if (strcmp(args[j], ">") == 0) {
			// last without filename or duplicated
			if (j + 1 == numTokens || out > 0) {
				ioError = 1;
				break;
			}
			out = 1;
			out_file = strdup(args[j + 1]);
			found = 1;
		} else if (strcmp(args[j], ">>") == 0) {
			// last without filename or duplicated
			if (j + 1 == numTokens || out > 0) {
				ioError = 1;
				break;
			}
			out = 2;
			out_file = strdup(args[j + 1]);
			found = 1;
		} else if (strcmp(args[j], "2>") == 0) {
			if (j + 1 == numTokens || err > 0) {
				ioError = 1;
				break;
			}
			err = 1;
			err_file = strdup(args[j + 1]);
			found = 1;
		}
		if (found == 1) {
			// set to NULL
			args[j] = NULL;
			args[j + 1] = NULL;
			j += 2;
		} else {
			j++;
			}
		}

	// check ioError and same filename
	if (ioError == 1 
		|| (in > 0 && out > 0 && strcmp(in_file, out_file) == 0)
		|| (in > 0 && err > 0 && strcmp(in_file, err_file) == 0)
		|| (out > 0 && err > 0 && strcmp(out_file, err_file) == 0)) {
		fprintf(stderr, RD_ERR);
		exit(EXIT_FAILURE);
	}

	// obtain fd
	int in_fd = -1, out_fd = -1, err_fd = -1;

	int dupError = 0;
	if (in == 1 && dupError == 0) {
		in_fd = open(in_file, O_RDONLY, 0);
		if (in_fd < 0) {
			dupError = 1;
		}
	}
	if (out == 1 && dupError == 0) {
		out_fd = open(out_file, O_CREAT | O_WRONLY, 0644);
		if (out_fd < 0) {
			dupError = 1;
		}
	} else if (out == 2 && dupError == 0) {
		out_fd = open(out_file, O_CREAT | O_WRONLY | O_APPEND, 0644);
		if (out_fd < 0) {
			dupError = 1;
		}
	}
	if (err == 1 && dupError == 0) {
		err_fd = open(err_file, O_CREAT | O_WRONLY, 0644);
		if (err_fd < 0) {
			dupError = 1;
		}
	}

	// perform dup2
	if (dupError == 1) {
		fprintf(stderr, RD_ERR);
		exit(EXIT_FAILURE);
	} else {
		if (in_fd > 0) {
			dup2(in_fd, STDIN_FILENO);
		}
		if (out_fd > 0) {
			dup2(out_fd, STDOUT_FILENO);
		}
		if (err_fd > 0) {
			dup2(err_fd, STDERR_FILENO);
		}
	}
}

void signal_handler(int sig) {
	if (sig == SIGCHLD) {
		conditional_flag = 1;
	} else if (sig == SIGUSR1) {
		node_t *node = bg_list.head;
		while (node != NULL) {
			printBGPEntry(node->value);
			node = node->next;
		}
	}
}

int countNumOfPrograms(char *args[], size_t numTokens) {
	int numOfPrograms = 1;
	int i = 0;
	for (i = 0; i < numTokens; ++i) {
		if (strcmp(args[i], "|") == 0) {
			numOfPrograms += 1;
		}
	}
	return numOfPrograms;
}

void buildCommands(char *command, char **commands, const size_t numOfPrograms) {
	// split command by '|' and store it into commands
	int i = 0;
	for (i = 0; i < numOfPrograms; i++) {
		*(commands + i) = strdup(strsep(&command, "|&"));
	}
}
