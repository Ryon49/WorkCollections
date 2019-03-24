import React from 'react'
import {Button, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row} from "reactstrap";
import {ModalType} from "../../config";

class StarModal extends React.Component {
    constructor(props) {
        super(props);

        this.onClick = this.onClick.bind(this);
    }

    onClick(e, type, data, complete) {
        e.preventDefault();
        if (e.stopPropagation)
            e.stopPropagation();
        this.props.toggleModal(type, data, complete, true);
    }

    render() {
        const star = this.props.data;
        return <Modal isOpen={this.props.show} toggle={this.props.close} className={this.props.className}>
            <ModalHeader tag={'h3'} toggle={this.props.close}>{star.name}</ModalHeader>
            <ModalBody>
                <p>Born in: {star.birthYear === 0 ? 'unknown' : star.birthYear}</p>

                <br/>
                <h5>
                    <Row>
                        <Col xs='8'>Movies</Col>
                        <Col xs='4'>Year</Col>
                    </Row>
                </h5>
                <hr/>
                {star.movies.map((movie, k) => <div key={k} style={{margin: '15px 0 15px 0'}}>
                    <Row>
                        <Col xs='8'>
                            <a className='font-weight-bold'
                               href={'api/movie/find/' + movie.id}
                               onClick={(e) =>
                                   this.onClick(e, ModalType.MOVIE, {id: movie.id}, false)}>
                                {movie.title}
                            </a>
                        </Col>
                        <Col xs='4'>{movie.year}</Col>
                    </Row>
                </div>)}

            </ModalBody>
            <ModalFooter>
                <Button color="secondary" onClick={this.props.close}>Close</Button>
            </ModalFooter>
        </Modal>;
    }
}

export default StarModal;
