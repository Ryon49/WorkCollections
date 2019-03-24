import React from 'react'
import './Card.css'
import {Button, Col, Row, Tooltip} from "reactstrap";
import {CardCheckForm} from "./";

class CardCheck extends React.Component {
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
        return <section className="checkout-creditcard">
            <Row>
                <Col xs='1' sm='1' md='1' lg='1' xl='1'>
                    <Button id='stopCheckingBtn' className='stopChecking text-center'
                            color='link' size='sm' onClick={this.props.stopChecking}>
                        <i className="fas fa-caret-left fa-2x"/>
                    </Button>
                    <Tooltip placement='top-end' target='stopCheckingBtn'
                             isOpen={this.state.tooltipOpen} toggle={this.toggleTooltip}>
                        Back to cart
                    </Tooltip>
                </Col>
                <Col xs='11' sm='11' md='11' lg='11' xl='11'>
                    <h3>Credit Card</h3>
                    <hr/>
                </Col>
            </Row>
            <CardCheckForm user={this.props.user} generateReceipt={this.props.generateReceipt}/>
        </section>
    }
}

export default CardCheck;
