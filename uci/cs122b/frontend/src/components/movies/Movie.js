import React, {Component} from 'react'
import {Col, Row} from "reactstrap";
import {ModalType, BASE_URL} from '../../config'
import MovieRating from "../ratings";
import {AddToCart} from "../cart/";

class Movie extends Component {
    constructor(props) {
        super(props);
        this.state = {
            toggle_active: true
        };
        this.toggleCollapse = this.toggleCollapse.bind(this);
        this.onClick = this.onClick.bind(this);
        this.searchByGenre = this.searchByGenre.bind(this);
    }

    toggleCollapse() {
        this.setState({
            toggle_active: !this.state.toggle_active,
        });
    }

    onClick(e, type, data, complete) {
        e.preventDefault();
        if (e.stopPropagation)
            e.stopPropagation();
        this.props.toggleModal(type, data, complete, true);
    }

    searchByGenre(e, genreId) {
        e.preventDefault();
        if (e.stopPropagation)
            e.stopPropagation();
        this.props.requestMovies('http://' + BASE_URL + '/Fablix/api/movie/genre',
            {genreId: genreId, pageNum: 1}, false, true);
    }

    render() {
        let movie = this.props.movie;
        return <div className='container card-container'>
            <Row className='card-header' role='tab' onClick={this.toggleCollapse}>
                <Col xs='6' sm='6' md='6' lg='7' xl='7'>
                    {this.props.idx}.&nbsp;
                    <a className='font-weight-bold'
                       href={'api/movie/find/' + movie.id}
                       onClick={(e) => this.onClick(e, ModalType.MOVIE, movie, true)}>{movie.title}</a>
                    &nbsp;<sub>({movie.year})</sub>
                </Col>
                <Col  xs='4' sm='4' md='4' lg='3' xl='3'>
                    <MovieRating
                        rating={movie.rating}
                        className='text-center justify-content-center align-self-center'
                        style={{textAlign: 'center'}}/>
                </Col>
                <Col xs='1' sm='1' md='1' lg='1' xl='1'>
                    <AddToCart movieName={movie.title} movieId={movie.id}
                               updateShoppingCart={() =>
                                   this.props.updateShoppingCart(
                                       {id: movie.id, title: movie.title, genres: movie.genres})}/>
                </Col>
            </Row>

            <div style={this.state.toggle_active ? {display: 'None'} : {display: 'inherit'}}
                 role='tabpanel'>

                <div className='card-block'>
                    <Row style={{margin: '10px 0 10px 0'}}>
                        <Col xs='12' sm='12' md='12' lg='6' xl='6'>
                            Director: {movie.director}
                        </Col>
                        <Col xs='0' sm='0' md='0' lg='1' xl='1'/>
                        <Col xs='12' sm='12' md='12' lg='5' xl='5'>
                            {movie.genres.length === 0 ? 'No genre specified' : <React.Fragment>
                                Genre: {movie.genres.map((g, k) =>
                                <a key={k} href={'api/movie/genre?genreId=' + g.id}
                                   onClick={(e) => this.searchByGenre(e, g.id)}>
                                    {g.name}
                                </a>).reduce((prev, curr) => [prev, ', ', curr])}
                            </React.Fragment>}
                        </Col>
                    </Row>
                    <Row style={{margin: '0 0 10px 0'}}>
                        <Col xs='12' sm='12' md='2' lg='1' xl='1'>Actors:</Col>
                        <Col xs="12" sm='12' md='10' lg='10' xl='10'>
                            {movie.stars.map((star, i) => <span key={i}>
                                <a href={'api/star/find/' + star.id}
                                   onClick={(e) => this.onClick(e, ModalType.STAR, {id: star.id}, false)}>{star.name}</a>
                                &nbsp;&nbsp;&nbsp;</span>)}
                        </Col>
                    </Row>
                </div>
            </div>
        </div>
    };
}

export default Movie;
