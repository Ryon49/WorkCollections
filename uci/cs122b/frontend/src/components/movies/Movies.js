import React from 'react'
import {Movie, SortController, MaxRecordController} from './'
import './Movies.css';
import {Col, Row} from "reactstrap";

class Movies extends React.Component {
    // componentWillMount() {
    //     // let requestUrl = 'http://' + window.location.host.split(":")[0] + ':8080/Fablix/api/movie/top20';
    //     let requestUrl = 'http://' + window.location.host.split(":")[0] + ':8080/Fablix/api/movie/find';
    //
    //     this.props.requestMovies(requestUrl, {pageNum: 1, maxRecords: this.props.maxRecords}, false);
    // }

    render() {
        return <React.Fragment>
            {/*<Row className='grid-movies-description'>*/}
                {/*<Col xs='12' sm='12' md='12' lg='12' xl='12'>Result Description</Col>*/}
            {/*</Row>*/}
            <Row className='grid-movies-control'>
                <Col className='nopadding' tag='span' xs='1' sm='1' md='0' lg='1' xl='1'/>
                <Col className='nopadding' tag='span' xs='6' sm='6' md='6'
                     lg='5' xl='5'>
                    <SortController requestMovies={this.props.requestMovies} sortBy={this.props.sortBy} />
                </Col>
                <Col tag='span' xs='5' sm='5' md='5' lg='4' xl='4'>
                    <MaxRecordController requestMovies={this.props.requestMovies} maxRecords={this.props.maxRecords}/>
                </Col>
            </Row>

            {this.props.movies.map((movie, idx) => <Movie
                key={idx} idx={idx + 1}
                movie={movie} toggleModal={this.props.toggleModal}
                requestMovies={this.props.requestMovies}
                updateShoppingCart={this.props.updateShoppingCart}/>
            )}
            {/*{movies}*/}
        </React.Fragment>
    }
}

export default Movies;

