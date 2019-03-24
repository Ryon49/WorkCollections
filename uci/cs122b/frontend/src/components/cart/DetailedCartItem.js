import React from 'react'
import './ShoppingCart.css'
import {Col, ListGroupItem, Row, Tooltip} from "reactstrap";
import {ChangeQuantity} from "./index";

class DetailedCartItem extends React.Component {
    constructor(props) {
        super(props);

        this.toggleTooltip = this.toggleTooltip.bind(this);
        this.state = {
            tooltipOpen: false
        };
    }

    toggleTooltip() {
        this.setState({
            tooltipOpen: !this.state.tooltipOpen
        });
    }

    render() {
        let movie = this.props.movie;
        return <ListGroupItem className='cart-item'>
            <Row>
                <Col xs='0' sm='0' md='1' lg='1' xl='1'>{this.props.idx + 1}</Col>
                <Col xs='6' sm='6' md='5' lg='5' xl='5'>{movie.title}</Col>
                <Col xs='3' sm='3' md='3' lg='3' xl='3' className='text-wrap'>{getGenres(movie.genres)}</Col>
                <Col xs='2' sm='2' md='2' lg='1' xl='1'>
                    <ChangeQuantity width='60px' quantity={movie.quantity}
                                    updateShoppingCart={this.props.updateShoppingCart}/>
                </Col>
                <Col xs='1' sm='1' md='1' lg={{size: 1, offset: 1}} xl={{size: 1, offset: 1}}>
                    <i className="fas fa-times" id={'DeleteTooltip_' + this.props.idx}
                       onClick={() => this.props.updateShoppingCart(0)}
                    />
                </Col>
                <Tooltip placement='bottom-start' target={'DeleteTooltip_' + this.props.idx}
                         isOpen={this.state.tooltipOpen} toggle={this.toggleTooltip}>
                    Delete
                </Tooltip>
            </Row>
        </ListGroupItem>
    }
}

function getGenres(genres) {
    return genres.map(g => g.name).join(', ')
}

export default DetailedCartItem
