import React from 'react'
import {Tooltip} from "reactstrap";

class AddToCart extends React.Component {
    constructor(props) {
        super(props);
        this.onClick = this.onClick.bind(this);

        this.toggleTooltip = this.toggleTooltip.bind(this);
        this.state = {
            id: `tooltip-${Math.floor(Math.random() * 2000) + Math.floor(Math.random() * 1234)}`,
            tooltipOpen: false
        };
    }

    toggleTooltip() {
        this.setState({
            tooltipOpen: !this.state.tooltipOpen
        });
    }

    onClick(e) {
        e.preventDefault();
        if (e.stopPropagation)
            e.stopPropagation();
        this.props.updateShoppingCart()
    }

    render() {
        return <span id={this.state.id}>
            <i onClick={this.onClick}
               className="fas fa-shopping-cart"/>
            <Tooltip placement="top-start" isOpen={this.state.tooltipOpen}
                     target={this.state.id} toggle={this.toggleTooltip}>
                Add to Cart
            </Tooltip>
        </span>
    }
}

export default AddToCart;
