import React from 'react'
import {Input} from "reactstrap";

class ChangeQuantity extends React.Component {
    constructor(props) {
        super(props);

        this.onChange = this.onChange.bind(this);
        this.handleKeyPress = this.handleKeyPress.bind(this);

        this.state = {
            quantity: this.props.quantity,
            previousQuantity: this.props.quantity,
        }
    }

    componentWillReceiveProps(nextProps) {
        if (this.state.quantity !== nextProps.quantity) {
            this.setState({
                quantity: nextProps.quantity,
            })
        }
    }

    onChange = (e) => {
        let value = e.target.value;
        if (value === '' || value === '0') {
            this.setState({
                quantity: 0,
                previousQuantity: this.state.quantity,
            });
            return
        }

        if (isNumeric(value)) {
            let inputValue = Number(value);
            console.log("CQ value: " + String(inputValue));

            if (inputValue === 0) {
                return;
            }
            this.props.updateShoppingCart(inputValue);
        } else {
            e.target.value = this.state.quantity
        }
    };

    handleKeyPress = (e) => {
        if (e.key === 'Enter') {
            this.props.updateShoppingCart(Number(e.target.value))
        }
    };

    onFocusOut = (e) => {
        if (e.target.value === '' || e.target.value === '0') {
            this.setState({
                quantity: this.state.previousQuantity,
            });
        }
    };

    render() {

        let width = this.props.width === undefined ? 'inherit' : this.props.width;
        return <Input type='text' bsSize='sm' value={this.state.quantity} style={{width: width}}
                      onKeyPress={this.handleKeyPress} onChange={this.onChange}
                      onBlur={this.onFocusOut}/>
    }
}

function isNumeric(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}

export default ChangeQuantity;
