import * as React from "react";
import './HomePage.css'
import {Button, Col, ListGroup, ListGroupItem, Row} from "reactstrap";
import {BASE_URL, ModalType} from "../config";

class HomePage extends React.Component {
    constructor(props) {
        super(props);

        this.onClick = this.onClick.bind(this);
        this.simpleMovie = this.simpleMovie.bind(this);
        this.searchByLetter = this.searchByLetter.bind(this);
        this.searchByGenre = this.searchByGenre.bind(this);
    }

    onClick(e, type, data, complete) {
        e.preventDefault();
        if (e.stopPropagation)
            e.stopPropagation();
        this.props.toggleModal(type, data, complete, true);
    }

    simpleMovie(idx, movie) {
        return <ListGroupItem key={idx}>
            <Row>
                <Col xs='12' sm='8' md='10' lg='10' xl='10' className='text-left'>
                    &nbsp;&nbsp;{idx + 1}.&nbsp;
                    <a className='font-weight-bold home-movie-text' href={'api/movie/find/' + movie.id}
                       onClick={(e) => this.onClick(e, ModalType.MOVIE, movie, true)}>{movie.title}</a>
                </Col>
                <Col xs='12' sm='4' md='2' lg='2' xl='2'>
                    <i key={0} className='fa fa-star'/>{movie.rating.rating}
                </Col>
            </Row>
        </ListGroupItem>
    }

    searchByLetter(e) {
        this.props.requestMovies('http://' + BASE_URL + '/Fablix/api/movie/letter',
            {letter: e.target.value, pageNum: 1}, false, true);
    }

    searchByGenre(e) {
        // alert(e.target.value)
        this.props.requestMovies('http://' + BASE_URL + '/Fablix/api/movie/genre',
            {genreId: e.target.value, pageNum: 1}, false, true);
    }

    render() {
        return (
            <React.Fragment>
                <section className="home-browser">
                    {/*<Button onClick={() => {this.props.history.push('/search')}}>Click</Button>*/}
                    <div className="home-browser-letters">
                        <h4>Browser by Letters:</h4>
                        {this.props.movieLetter.map((letter, key) =>
                            <Button key={key} size='sm' onClick={this.searchByLetter} value={letter}>{letter}</Button>)}
                    </div>
                    <hr/>
                    <div className="home-browser-genres">
                        <h4>Browser by Genres:</h4>
                        {this.props.genreList.map((genre, key) =>
                            <Button key={key} onClick={this.searchByGenre} value={genre.id}>{genre.name}</Button>)}
                    </div>
                </section>
                <section className="home-top">
                    <p>Top 20 Rated Movies:</p>
                    <ListGroup>
                        {this.props.top20Movies.map((movie, idx) => this.simpleMovie(idx, movie))}
                    </ListGroup>
                </section>
                <section className="home-copyright text-center">© 2019 Ryon49</section>
            </React.Fragment>
        )
    }
}

export default HomePage;
