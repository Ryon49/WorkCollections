import React from 'react'
import './SearchPage.css'
import {Movies, Paginator, SearchForm, SimpleCart} from "../components";
import {BASE_URL, createFailureToast} from "../config";

class SearchPage extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            requestUrl: this.props.requestUrl,

            movies: this.props.movies,
            maxRecords: this.props.maxRecords,
            curPage: this.props.curPage,
            sortBy: this.props.sortBy,

            cart: this.props.cart
        };
    }

    componentWillReceiveProps(nextProps) {
        this.setState({
            movies: nextProps.movies,
            maxRecords: nextProps.maxRecords,
            sortBy: nextProps.sortBy,

            requestUrl: nextProps.requestUrl,
            curPage: nextProps.curPage,

            cart: nextProps.cart
        });
    }

    componentWillMount() {
        if (this.props.user === undefined || this.props.user === null) {
            createFailureToast("Please login first");
            this.props.history.push('/');
            return
        }
        if (this.state.movies.length === 0) {
            let requestUrl = 'http://' + BASE_URL + '/Fablix/api/movie/find';
            this.props.requestMovies(requestUrl, {pageNum: 1, maxRecords: this.props.maxRecords}, false, true);
        }
    }

    render() {
        return (
            <React.Fragment>
                <section className="search-control">
                    <SearchForm requestMovies={this.props.requestMovies}/>
                    <hr/>
                    <SimpleCart cart={this.state.cart} updateShoppingCart={this.props.updateShoppingCart}
                                onClick={() => this.props.history.push('/checkout')}/>
                </section>
                <section className="search-movies"
                         style={{gridTemplateRows: 'repeat(' + String(this.state.movies.length + 2) + ',min-content)'}}>
                    <div>Search Description: {this.props.searchDescription}</div>
                    <Movies movies={this.state.movies}
                            toggleModal={this.props.toggleModal} requestMovies={this.props.requestMovies}
                            updateShoppingCart={this.props.updateShoppingCart} maxRecords={this.state.maxRecords}
                            sortBy={this.state.sortBy}
                    />
                </section>
                <section className="search-paging">
                    <Paginator url={this.state.requestUrl} curPage={this.state.curPage}
                               requestMovies={this.props.requestMovies}/>
                </section>
                <footer className="search-copyright text-center">© 2019 Ryon49</footer>
            </React.Fragment>
        );
    }
}

export default SearchPage;
