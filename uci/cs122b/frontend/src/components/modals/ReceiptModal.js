import React from 'react'
import {Button, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row} from "reactstrap";

class ReceiptModal extends React.Component {
    constructor(props) {
        super(props);

        this.redirect = this.redirect.bind(this);
        // this.state = {
        //     receiptIds: this.props.data.receiptIds
        // };
    }

    redirect = (page) => {
        this.props.close();
        this.props.redirectTo(page)
    };

    render() {
        let notYetCheckout = this.props.receiptIds.length === 0;
        return <Modal isOpen={this.props.show} toggle={this.props.close}>
            <ModalHeader tag={'h3'} toggle={this.props.close}>
                {notYetCheckout ? "Preview Receipt" :
                    "Your receipt"}
            </ModalHeader>
            <ModalBody>
                <p>Card holder: {getPrintName(this.props.data.user)}</p>
                <p>Card No: ********</p>
                <br/>
                <h5>
                    <Row>
                        {notYetCheckout ?
                            <Col xs='12' sm='12' md={{size: 8, offset: 1}} lg={{size: 8, offset: 1}}
                                 xl={{size: 8, offset: 1}}>
                                Title
                            </Col> :
                            <React.Fragment>
                                <Col xs='12' sm='12' md='3' lg='3' xl='3'>
                                    Sale Id:
                                </Col>
                                <Col xs='12' sm='12' md={{size: 6}} lg={{size: 6}}
                                     xl={{size: 6}}>
                                    Title
                                </Col>
                            </React.Fragment>
                        }
                        
                        <Col xs='12' sm='12' md='3' lg='3' xl='3'>Qty</Col>
                    </Row>
                </h5>
                <hr/>
                {this.props.data.cart.map((m, k) =>
                    <div key={k} style={{margin: '15px 0 15px 0'}}>
                        <Row>
                            {notYetCheckout ?
                                <Col xs='12' sm='12' md={{size: 8, offset: 1}} lg={{size: 8, offset: 1}}
                                     xl={{size: 8, offset: 1}}>
                                    {m.title}
                                </Col> :
                                <React.Fragment>
                                    <Col xs='12' sm='12' md='3' lg='3' xl='3'>
                                        {this.props.receiptIds[k]}
                                    </Col>
                                    <Col xs='12' sm='12' md={{size: 6}} lg={{size: 6}}
                                         xl={{size: 6}}>
                                        {m.title}
                                    </Col>
                                </React.Fragment>
                            }
                            <Col xs='12' sm='12' md='3' lg='3' xl='3'>&nbsp;&nbsp;{m.quantity}</Col>
                        </Row>
                    </div>)}

            </ModalBody>
            <ModalFooter>
                {notYetCheckout ? "" :
                    <React.Fragment>
                        <Button color="primary" onClick={() => this.redirect("")}>Back to Home</Button>{' '}
                        <Button color="primary" onClick={() => this.redirect("search")}>Continue Searching</Button>
                    </React.Fragment>}

                <Button color="secondary" onClick={this.props.close}>Close</Button>{' '}

                {!notYetCheckout ? "" :
                    <Button color="primary" onClick={this.props.checkoutShoppingCart}>Confirm</Button>}

                {/*<Button color="secondary" onClick={this.props.close}>Close</Button>{' '}*/}
                {/*<Button color="primary" onClick={this.props.checkoutShoppingCart}>Confirm</Button>*/}
            </ModalFooter>
        </Modal>
    }
}

function getPrintName(user) {
    return user.lastName + ", " + user.firstName
}

export default ReceiptModal
