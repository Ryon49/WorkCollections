import React from 'react'
import {Button, Form, FormGroup, Input, InputGroup, Modal, ModalBody, ModalFooter, ModalHeader} from "reactstrap";

import {ReCaptcha} from 'react-recaptcha-google'
import {createFailureToast, BASE_URL} from "../../config";

class LoginModal extends React.Component {
    constructor(props) {
        super(props);
        this.onSubmit = this.onSubmit.bind(this);
        this.onCustomerSubmit = this.onCustomerSubmit.bind(this);
        this.onEmployeeSubmit = this.onEmployeeSubmit.bind(this);
        this.verifyCallback = this.verifyCallback.bind(this);

        this.captcha = React.createRef();
        this.captchaInput = React.createRef();
        this.state = {
            errMsg: '',

            ready: false
        }
    }

    onSubmit(e) {
        e.preventDefault();

        if (this.props.isCustomer) {
            this.onCustomerSubmit(e)
        } else {
            this.onEmployeeSubmit(e)
        }
    }

    onCustomerSubmit(e) {
        let data = new FormData();

        let email = e.target.email.value;
        if (email.length === 0) {
            this.setState({
                errMsg: "Username not found"
            });
            return
        }

        let password = e.target.password.value;
        if (password.length === 0) {
            this.setState({
                errMsg: "Incorrect password"
            });
            return
        }

        // let captcha = e.target.captcha.value;
        // if (captcha.length === 0) {
        //     this.setState({
        //         errMsg: "Are you a robot?"
        //     });
        //     return
        // }

        data.append('email', email);
        data.append('password', password);
        // data.append('recaptchaToken', captcha);

        fetch('http://' + BASE_URL + '/Fablix/api/user/login',
            {
                method: "POST",
                body: data
            })
            .then(response => response.json())
            .then(json => {
                console.log(json);
                if (json.success) {
                    this.setState({
                        errMsg: ''
                    });
                    this.props.updateUser(json.data.customer);
                    this.props.close()
                } else {
                    // do update error message here
                    this.setState({
                        errMsg: json.errMsg
                    });
                    this.captcha.current.reset()
                }
            }).catch(error => createFailureToast("It seems like a network problem has occurred"));
    }

    onEmployeeSubmit(e) {
        let data = new FormData();

        let email = e.target.email.value;
        if (email.length === 0) {
            this.setState({
                errMsg: "Username not found"
            });
            return
        }

        let password = e.target.password.value;
        if (password.length === 0) {
            this.setState({
                errMsg: "Incorrect password"
            });
            return
        }

        data.append('email', email);
        data.append('password', password);

        fetch('http://' + BASE_URL + '/Fablix/api/employee/login',
            {
                method: "POST",
                body: data
            })
            .then(response => response.json())
            .then(json => {
                console.log(json);
                if (json.success) {
                    this.setState({
                        errMsg: ''
                    });
                    this.props.updateUser(json.data.employee);
                } else {
                    // do update error message here
                    this.setState({
                        errMsg: json.errMsg
                    });
                }
            }).catch(error => createFailureToast("It seems like a network problem has occurred"));
    }

    verifyCallback(recaptchaToken) {
        // Here you will get the final recaptchaToken!!!
        this.captchaInput.current.value = recaptchaToken;
        console.log(recaptchaToken, "<= your recaptcha token");
    }

    render() {
        return <Modal size='sm' isOpen={this.props.show}>
            <Form method="POST" onSubmit={this.onSubmit}>
                <ModalHeader tag={'h3'} toggle={this.props.close}>
                    {this.props.isCustomer ? 'Welcome to Fablix' : 'Employee login'}
                </ModalHeader>
                <ModalBody>
                    <p className="text-danger text-center">{this.state.errMsg}</p>
                    <FormGroup>
                        <InputGroup>
                            <div className="input-group-prepend">
                                    <span className="input-group-text"><i
                                        className="fa fa-user text-secondary"/> </span>
                            </div>
                            <Input type="email" name="email" placeholder="Email"/>
                        </InputGroup>
                    </FormGroup>
                    <FormGroup style={{marginBottom: 0}}>
                        <InputGroup>
                            <div className="input-group-prepend">
                                    <span className="input-group-text"><i
                                        className="fa fa-lock text-secondary"/> </span>
                            </div>
                            <Input type="password" name="password" placeholder="Password"/>
                        </InputGroup>
                        {/*{this.props.isCustomer ? <InputGroup style={{marginTop: '10px'}}>*/}
                            {/*<ReCaptcha*/}
                                {/*ref={this.captcha}*/}
                                {/*render="normal"*/}
                                {/*sitekey="6Lc3ApAUAAAAALWyySdUM4VXNlRGuF_e9P2py824"*/}
                                {/*verifyCallback={this.verifyCallback}/>*/}
                            {/*<input type='hidden' name="captcha" ref={this.captchaInput}/>*/}
                        {/*</InputGroup> : ''}*/}
                    </FormGroup>
                </ModalBody>
                <ModalFooter>
                    <Button size='sm' color="primary" className="btn btn-block">Login</Button>
                </ModalFooter>
            </Form>
        </Modal>
    }
}

export default LoginModal;
