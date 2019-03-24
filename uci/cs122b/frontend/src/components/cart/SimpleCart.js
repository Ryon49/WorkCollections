import React from 'react'
import './ShoppingCart.css'
import {Button, ListGroup} from "reactstrap";
import {SimpleCartItem} from "./";

class SimpleCart extends React.Component {
    constructor(props) {
        super(props);

        this.state = ({
            cart: this.props.cart
        })
    }

    componentWillReceiveProps(nextProps) {
        this.setState({
            cart: nextProps.cart
        })
    }

    render() {
        let cart = this.state.cart;
        // cart = cart.sort((a, b) => (a.title > b.title) ? 1 : ((b.title > a.title) ? -1 : 0));
        return <div>Shopping Cart:
            <ListGroup className='cart'>
                {cart.map((movie, idx) =>
                    <SimpleCartItem key={idx} movie={movie}
                                    updateShoppingCart={(quantity) =>
                                        this.props.updateShoppingCart(
                                            {id: movie.id}, quantity)}/>)}
            </ListGroup>
            {cart.length > 0 ?
                <div className='text-right'>
                    <Button size='sm' color="primary" onClick={this.props.onClick}>Checkout</Button>
                </div> : ''}
        </div>
    }
}

export default SimpleCart;
