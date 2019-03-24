import React from 'react'
import {Button, Col, Modal, ModalBody, ModalFooter, ModalHeader, Row} from "reactstrap";
import {ModalType} from "../../config";
import MovieRating from "../ratings";

class MovieModal extends React.Component {
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
        let movie = this.props.data;
        return <Modal isOpen={this.props.show} toggle={this.props.close} className={this.props.className}>
            <ModalHeader tag={'h3'} toggle={this.props.close}>{movie.title}</ModalHeader>
            <ModalBody>
                <p>Director: {movie.director}</p>
                <p>Genres: {movie.genres.map(g => g.name).join(' | ')}</p>
                <p>Rating: &nbsp;&nbsp;<MovieRating rating={movie.rating}/></p>

                <br/>
                <h5>
                    <Row>
                        <Col xs='8'>Actors</Col>
                        <Col xs='4'>Birth Year</Col>
                    </Row>
                </h5>
                <hr/>
                {movie.stars.map((star, k) =>
                    <div key={k} style={{margin: '15px 0 15px 0'}}>
                        <Row>
                            <Col xs='8'>
                                <a href={'api/star/find/' + star.id}
                                   onClick={(e) => this.onClick(e, ModalType.STAR, {id: star.id}, false)}>{star.name}</a>
                            </Col>
                            <Col xs='4'>{star.birthYear === 0 ? 'unknown' : star.birthYear}</Col>
                        </Row>
                    </div>)}

            </ModalBody>
            <ModalFooter>
                <Button color="primary"
                        onClick={() => this.props.updateShoppingCart({id: movie.id, title: movie.title, genres: movie.genres})}>Buy</Button>{' '}
                <Button color="secondary" onClick={this.props.close}>Close</Button>
            </ModalFooter>
        </Modal>
    }
}

export default MovieModal;
