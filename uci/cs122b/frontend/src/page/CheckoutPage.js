import * as React from "react";

import './CheckoutPage.css'
import {createFailureToast, ModalType} from "../config";
import {Button} from "reactstrap";
import {DetailedCart} from "../components/cart/";
import CardCheck from "../components/card/";

class CheckoutPage extends React.Component {
    constructor(props) {
        super(props);

        this.toggleCreditCheck = this.toggleCreditCheck.bind(this);
        this.stopChecking = this.stopChecking.bind(this);
        this.generateReceipt = this.generateReceipt.bind(this);

        this.state = {
            user: this.props.user,
            cart: this.props.cart,

            enableCreditCheck: false,
        };
    }

    componentWillMount() {
        if (this.props.user === undefined) {
            createFailureToast("Please login first");
            this.props.history.push('/');
        }
    }

    componentWillReceiveProps(nextProps, nextContext) {
        this.setState({
            cart: nextProps.cart
        })
    }

    toggleCreditCheck = () => {
        this.setState({
            enableCreditCheck: true
        })
    };

    stopChecking = () => {
        this.setState({
            enableCreditCheck: false
        })
    };

    generateReceipt = () => {
        let receipt = {
            user: this.state.user,
            cart: this.state.cart,
        };
        console.log(receipt);
        this.props.toggleModal(ModalType.RECEIPT, receipt, true, true)
    };

    render() {
        let disableCartStyle = {
            position: 'relative',
            left: '25%'
        };
        let creditCheck = this.state.enableCreditCheck;

        return (
            <React.Fragment>
                <div className="checkout-list" style={creditCheck ? {pointerEvents: 'none'} : disableCartStyle}>
                    {this.state.cart.length === 0 ? <EmptyCart/> :
                        <React.Fragment>
                            <h3>Item List</h3>
                            <hr/>
                            <DetailedCart cart={this.state.cart} updateShoppingCart={this.props.updateShoppingCart}/>
                            <div className='text-right'>
                                {/*<Button color='primary' disabled={creditCheck}*/}
                                        {/*onClick={() => this.props.toggleModal(ModalType.RECEIPT, receipt, true, true)}>*/}
                                    {/*Toggle Receipt*/}
                                {/*</Button>&nbsp;&nbsp;&nbsp;*/}
                                <Button color='primary' disabled={creditCheck}
                                        onClick={this.toggleCreditCheck}>Confirm</Button>
                            </div>
                        </React.Fragment>
                    }
                </div>
                {!creditCheck ? '' :
                    <React.Fragment>
                        <CardCheck stopChecking={this.stopChecking} user={this.state.user}
                                   generateReceipt={this.generateReceipt}/>
                        <section className="checkout-copyright text-center">© 2019 Ryon49</section>
                    </React.Fragment>}
            </React.Fragment>
        )
    }
}

function EmptyCart(props) {
    return <h5 className='text-center' style={{fontSize: '25px'}}>You have an <b>EMPTY</b> cart, go get something</h5>
}

export default CheckoutPage;
