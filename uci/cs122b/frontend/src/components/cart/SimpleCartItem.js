import React from 'react'
import {Col, ListGroupItem, Row} from "reactstrap";
import {ChangeQuantity} from "./";

class SimpleCartItem extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            movie: this.props.movie,
        }
    }

    componentWillReceiveProps(nextProps) {
        this.setState({
            movie: nextProps.movie,
        })
    }

    render() {
        let movie = this.state.movie;
        return <ListGroupItem className='cart-item'>
            <Row>
                <Col xs='12' sm='12' md='9' lg='9' xl='9' className='text-wrap nopadding'>{movie.title}</Col>
                <Col tag='div' xs='12' sm='12' md='3' lg='3' xl='3' className='nopadding'>
                    <ChangeQuantity quantity={movie.quantity} updateShoppingCart={this.props.updateShoppingCart}/>
                </Col>
            </Row>
        </ListGroupItem>
    }
}

export default SimpleCartItem;
