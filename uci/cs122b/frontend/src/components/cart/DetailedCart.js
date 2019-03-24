import React from 'react'
import './ShoppingCart.css'
import {Col, ListGroup, ListGroupItem, ListGroupItemHeading, Row} from "reactstrap";
import {DetailedCartItem} from "./";

class DetailedCart extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            user: this.props.user,
            cart: this.props.cart
        }
    }

    componentWillReceiveProps(nextProps) {
        this.setState({
            cart: nextProps.cart
        });
    }

    render() {
        return <ListGroup className='cart'>
                <ListGroupItem className='cart-item'>
                    <ListGroupItemHeading>
                        <Row>
                            <Col xs='0' sm='0' md='1' lg='1' xl='1'>#</Col>
                            <Col xs='6' sm='6' md='5' lg='5' xl='5'>Title</Col>
                            <Col xs='3' sm='3' md='3' lg='3' xl='3'>Genre</Col>
                            <Col xs='2' sm='2' md='2' lg='1' xl='1' className='half-size'>Qty</Col>
                            <Col xs='1' sm='1' md='1' lg='1' xl='1'/>
                        </Row>
                    </ListGroupItemHeading>
                </ListGroupItem>
                {this.state.cart.map((movie, idx) =>
                    <DetailedCartItem key={idx} movie={movie} idx={idx}
                                      updateShoppingCart={(quantity) =>
                                          this.props.updateShoppingCart(
                                              {id: movie.id}, quantity)}/>)}
            </ListGroup>
    }
}

export default DetailedCart;
