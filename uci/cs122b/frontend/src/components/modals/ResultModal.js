import React from 'react'
import {Col, Modal, ModalHeader, Row} from "reactstrap";
import ModalBody from "reactstrap/es/ModalBody";

class ResultModal extends React.Component {
    render() {
        let data = this.props.data
        return <Modal isOpen={this.props.show} toggle={this.props.close}>
            <ModalHeader tag={'h3'} toggle={this.props.close}>
                {data.type} Result
            </ModalHeader>
            <ModalBody>
                <h5>
                    <Row>
                        <Col xs='12' sm='12' md='9' lg='9' xl='9'>
                            Name
                        </Col>
                        <Col xs='12' sm='12' md='3' lg='3' xl='3'>
                            Id:
                        </Col>
                    </Row>
                </h5>
                <hr/>
                {Object.keys(data.ids).map((key, k) =>
                    <div key={k} style={{margin: '15px 0 15px 0'}}>
                        <Row>
                            <Col xs='12' sm='12' md='9' lg='9' xl='9'>
                                {data.ids[key]}
                            </Col>
                            <Col xs='12' sm='12' md='3' lg='3' xl='3'>
                                {key}
                            </Col>
                        </Row>
                    </div>)}

            </ModalBody>
        </Modal>
    }
}

export default ResultModal;
