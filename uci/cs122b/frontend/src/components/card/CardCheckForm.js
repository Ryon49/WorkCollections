import React from 'react'
import {Button, Form, FormFeedback, FormGroup, Input, InputGroup, InputGroupAddon} from "reactstrap";
import {BASE_URL, createFailureToast, createSuccessToast} from "../../config";

class CardCheckForm extends React.Component {
    constructor(props) {
        super(props);

        this.owner = React.createRef();
        this.cardNumber = React.createRef();
        this.exprDate = React.createRef();

        this.state = {
            ownerErr: [0, 0],
            cardNumberErr: [0, 0],
            exprDateErr: [0, 0],

            userVerified: false
        };

        this.onSubmit = this.onSubmit.bind(this);
    }

    onSubmit = (e) => {
        e.preventDefault();

        if (this.state.userVerified) {
            this.props.generateReceipt();
            return
        }

        let data = new FormData();

        let ownerErr = this.state.ownerErr;
        let cardNumberErr = this.state.cardNumberErr;
        let exprDateErr = this.state.exprDateErr;
        let hasError = false;

        let owner = this.owner.current.value.trim();
        if (owner === '') {
            ownerErr = [1, 1];
            hasError = true
        } else {
            ownerErr = [0, 0];
            let full_name = owner.split(" ");
            data.append('firstName', full_name[0]);
            data.append('lastName', full_name[1]);

        }
        let cardNumber = this.cardNumber.current.value.trim();
        if (cardNumber === '') {
            cardNumberErr = [1, 1];
            hasError = true
        } else {
            cardNumberErr = [0, 0];
            data.append('cardNumber', cardNumber);
        }

        let exprDate = this.exprDate.current.value.split('-');
        if (exprDate.length < 2) {
            exprDateErr = [1, 1];
            hasError = true
        } else {
            exprDateErr = [0, 0];
            data.append("exprYear", exprDate[0]);
            data.append('exprMonth', exprDate[1]);
        }

        if (hasError) {
            this.setState({
                ownerErr: ownerErr,
                cardNumberErr: cardNumberErr,
                exprDateErr: exprDateErr,

            })
        } else {
            fetch('http://' + BASE_URL + '/Fablix/api/user/credit', {
                method: "POST",
                body: data
            })
                .then(response => response.json())
                .then(json => {
                    console.log(json);
                    if (json.success) {
                        createSuccessToast("User verified");
                        this.setState({
                            ownerErr: [0, 0],
                            cardNumberErr: [0, 0],
                            exprDateErr: [0, 0],
                            userVerified: true,
                        });
                        this.props.generateReceipt();
                    } else {
                        let errorType = json.data.type;
                        if (errorType === 1) {
                            this.setState({
                                ownerErr: [1, 2],
                                cardNumberErr: [0, 0],
                                exprDateErr: [0, 0],
                            })
                        } else if (errorType === 2) {
                            this.setState({
                                ownerErr: [0, 0],
                                cardNumberErr: [1, 2],
                                exprDateErr: [0, 0],
                            })
                        } else if (errorType === 3) {
                            this.setState({
                                ownerErr: [0, 0],
                                cardNumberErr: [0, 0],
                                exprDateErr: [1, 2],
                            })
                        }
                    }
                }).catch(error => createFailureToast("It seems like a network problem has occurred"));
        }
    };

    render() {
        return <Form className='card-form' onChange={() => this.setState({userVerified: false})}>
            <p>Enter your credit card information:</p>
            <FormGroup>
                <InputGroup>
                    <InputGroupAddon addonType="prepend">Holder</InputGroupAddon>
                    <Input invalid={this.state.ownerErr[0] !== 0} placeholder='Holder name (Last, first)'
                           innerRef={this.owner}/>
                    <FormFeedback>{getErrorMessage(this.state.ownerErr[1], ownerErrMsg)}</FormFeedback>

                </InputGroup>
            </FormGroup>
            <FormGroup>
                <InputGroup>
                    <InputGroupAddon addonType="prepend">Card No.</InputGroupAddon>
                    <Input invalid={this.state.cardNumberErr[0] !== 0} placeholder='Card No.'
                           innerRef={this.cardNumber}/>
                    <FormFeedback>{getErrorMessage(this.state.cardNumberErr[1], cardNumberErrMsg)}</FormFeedback>
                </InputGroup>
            </FormGroup>
            <FormGroup>
                <InputGroup>
                    <InputGroupAddon addonType="prepend">Expr Date</InputGroupAddon>
                    <Input invalid={this.state.exprDateErr[0] !== 0} type='month' innerRef={this.exprDate} min="2001-01"
                           defaultValue='2019-01'/>
                    <FormFeedback>{getErrorMessage(this.state.exprDateErr[1], exprDateErrMsg)}</FormFeedback>
                </InputGroup>
            </FormGroup>

            <div className='text-center card-confirm-btn'>
                <Button color="primary" onClick={this.onSubmit}>Checkout</Button>
            </div>
        </Form>
    }
}

let ownerErrMsg = ['', 'Card holder information is missing', 'Card holder information is incorrect'];

let cardNumberErrMsg = ['', 'Card Number is missing', 'Card Number does not match'];

let exprDateErrMsg = ['', 'Expiration date field is empty', 'Expiration date information is incorrect'];

function getErrorMessage(type, choices) {
    return choices[type]
}

export default CardCheckForm
