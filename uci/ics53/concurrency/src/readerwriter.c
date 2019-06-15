#include "concurrency.h"

extern pthread_mutex_t cgMutex;
pthread_mutex_t rwMutex = PTHREAD_MUTEX_INITIALIZER;
int readCount = 0;

void snapshot_stats() {
    int i, sum = 0;
    int cfreq[HISTSIZE];
    statsnap s;
    s.n = 0;
    
    // part 2 put your locks here
    pthread_mutex_lock(&rwMutex);
    readCount++;
    if (readCount == 1) {
        pthread_mutex_lock(&cgMutex);
    }
    pthread_mutex_unlock(&rwMutex);


    s.mode = -1;
    int maxfreq = -1;
    for (i = 0; i < HISTSIZE; i++) {
        s.n += histogram[i];
        cfreq[i] = s.n;
        sum += i * histogram[i];        
        if (maxfreq < histogram[i]) {
            s.mode = i;
            maxfreq = histogram[i];
        }
    }

    // part 2 put your locks here
    pthread_mutex_lock(&rwMutex);
    readCount--;
    if (readCount == 0) {
        pthread_mutex_unlock(&cgMutex);
    }
    pthread_mutex_unlock(&rwMutex);

    s.mean = calc_mean_median(sum, s.n, cfreq, &s.median);
    // send stats
    Write(statpipe[1], &s, sizeof(statsnap));
}

void snapshot_histogram() {
    int i;
    histsnap s;
    s.n = 0;
    
    // part 2 put your locks here
    pthread_mutex_lock(&rwMutex);
    readCount++;
    if (readCount == 1) {
        pthread_mutex_lock(&cgMutex);
    }
    pthread_mutex_unlock(&rwMutex);

    for (i = 0; i < HISTSIZE; i++) {
        s.n += histogram[i];
        s.hist[i] = histogram[i];
    }
    
    // part 2 put your locks here
    pthread_mutex_lock(&rwMutex);
    readCount--;
    if (readCount == 0) {
        pthread_mutex_unlock(&cgMutex);
    }
    pthread_mutex_unlock(&rwMutex);

    // send n and histogram   
    Write(histpipe[1], &s, sizeof(histsnap));
}

void *readerwriter_stat_task(void *data) {
    while(1) {
        usleep(READSLEEP);
        snapshot_stats();
    }
    return NULL;
}

void *readerwriter_hist_task(void *data) {
    while(1) {
        usleep(READSLEEP);
        snapshot_histogram();
    }
    return NULL;
}

void start_readers(pthread_t *readers) {
    pthread_create(&readers[0], NULL, readerwriter_stat_task, NULL);
    pthread_create(&readers[1], NULL, readerwriter_hist_task, NULL);

}

void readerwriter(char *dirname) {
    // start reader threads
    pthread_t readers[2];
    start_readers(readers);

    // your code here
    concurrent_fg(dirname);

    // remember to cancel the reader threads when you are done processing all the files
    pthread_cancel(readers[0]);
    pthread_cancel(readers[1]);
}
