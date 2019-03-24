import React from "react";
import {BrowserRouter as Router, Link, Route} from "react-router-dom";
import './App.css';
import {AppModal, User} from "./components";
import {createFailureToast, createSuccessToast, ModalType, BASE_URL} from "./config";
import {CheckoutPage, HomePage, SearchPage} from "./page";
import {SearchBar} from "./components/search";
import {Button, Col, Row} from "reactstrap";

class App extends React.Component {
    constructor(props) {
        super(props);
        this.toggleModal = this.toggleModal.bind(this);
        this.closeModal = this.closeModal.bind(this);
        this.updateUser = this.updateUser.bind(this);
        this.requestMovies = this.requestMovies.bind(this);
        this.updateShoppingCart = this.updateShoppingCart.bind(this);
        this.checkoutShoppingCart = this.checkoutShoppingCart.bind(this);
        this.redirectTo = this.redirectTo.bind(this);
        this.toggleSearch = this.toggleSearch.bind(this);
        this.requestMoviesByTitle = this.requestMoviesByTitle.bind(this);

        this.homeRouter = React.createRef();

        // initialize modal
        let user = sessionStorage.getItem("user");

        if (user !== null) {
            user = JSON.parse(user)
        } else {
            user = undefined
        }

        this.state = {
            showSearchBar: false,

            user: user,
            cart: [],

            requestUrl: "",
            requestParams: {},
            maxRecords: 10,
            sortBy: 0,

            movies: [],
            curPage: [-1, -1],
            searchDescription: "Default Search",

            modalShow: false,
            modalType: ModalType.NONE,
            modalData: undefined,

            movieLetter: [],
            genreList: [],
            top20Movies: [],

            receiptIds: [],
        }
    }

    componentWillMount() {
        fetch('http://' + BASE_URL + '/Fablix/api/basic/info', {
            method: "GET",
        }).then(response => {
            return response.json()
        }).then(json => {
            let movieLetter = json.data.letters;
            let genreList = json.data.genreList;
            let top20Movies = json.data.top20Movies;
            this.setState({
                movieLetter: movieLetter,
                genreList: genreList,
                top20Movies: top20Movies,
            });
        }).catch(error => createFailureToast("It seems like a network problem has occurred"));
    }

    componentDidMount() {
        document.title = "Fablix"
    }


    async toggleModal(type, data, complete, requireLogin) {
        if (requireLogin && this.state.user === undefined) {
            createFailureToast("Please login first");
            return;
        }

        // close existing modal
        this.closeModal();

        if (complete === false) {

            if (type === ModalType.MOVIE || type === ModalType.STAR) {
                await fetch('http://' + BASE_URL + '/Fablix/api/' + type + '/lookup/' + data.id)
                    .then(response => {
                        console.log(response);
                        return response.json()
                    })
                    .then(json => {
                        this.setState({
                            modalShow: true,
                            modalType: type,
                            modalData: json,
                        })
                    }).catch(error => createFailureToast("It seems like a network problem has occurred"));

            }
        } else {
            this.setState({
                modalShow: true,
                modalType: type,
                modalData: data,
            })
        }
    }

    closeModal() {
        if (this.state.modalShow === true) {
            this.setState({
                modalShow: false,
            })
        }
    }

    updateUser = (user) => {
        if (user === undefined) {
            sessionStorage.removeItem("user");
            this.homeRouter.current.context.router.history.push('/');
            this.setState({
                user: user,
                cart: [],
                movies: [],
            });
        } else {
            sessionStorage.setItem("user", JSON.stringify(user));
            this.setState({
                user: user,
            });
        }
    };

    requestMovies = (requestUrl, params, usePrevious, requireLogin) => {
        if (requireLogin && this.state.user === undefined) {
            createFailureToast("Please login first");
            return;
        }

        let ps = {};
        ps['maxRecords'] = this.state.maxRecords;
        if (usePrevious) {
            requestUrl = this.state.requestUrl;
            ps = this.state.requestParams;

            Object.keys(params).forEach(key => {
                ps[key] = params[key]
            })
        } else {
            ps = params
        }

        let ps_str = Object.keys(ps).map(k => k + '=' + String(ps[k])).join('&');
        console.log(requestUrl + "?" + ps_str);
        fetch(requestUrl + "?" + ps_str, {
            method: "GET",
        })
            .then(response => {
                console.log(response);
                return response.json()
            })
            .then(json => {
                let data = json.data;
                console.log(data);
                this.setState({
                    requestUrl: requestUrl,
                    requestParams: ps,
                    maxRecords: data.maxRecords,
                    movies: data.movies,
                    searchDescription: data.searchDescription,
                    curPage: [data.pageNum + 1, data.totalPages],
                    sortBy: data.sortBy
                });
                this.homeRouter.current.context.router.history.push('/search');
            }).catch(error => createFailureToast("It seems like a network problem has occurred"));
    };

    requestMoviesByTitle(title) {
        let requestUrl = 'http://' + BASE_URL + '/Fablix/api/movie/find';
        this.requestMovies(requestUrl, {pageNum: 1, maxRecords: this.state.maxRecords, title: title}, false, true);
    }

    updateShoppingCart = (movie, quantity) => {
        if (this.state.user === undefined) {
            createFailureToast("Please login first");
            return
        }

        let cart = this.state.cart;
        let index = findIndexById(cart, movie.id);
        if (index !== -1) {
            if (quantity === undefined) {
                cart[index].quantity = cart[index].quantity + 1;
                createSuccessToast('Action Successful');

            } else {
                if (quantity === 0) {
                    cart = cart.filter(i => i.id !== movie.id)
                } else {
                    cart[index].quantity = quantity;
                }
            }
        } else {
            movie.quantity = 1;
            cart.push(movie);
            createSuccessToast('Action Successful');
        }

        this.setState({
            cart: cart,
            receiptIds: []
        });
    };

    async checkoutShoppingCart() {
        let data = new FormData();

        data.append("customerId", this.state.user.id);

        let movies = [];
        for (let m of this.state.cart) {
            movies.push({id: m.id, quantity: m.quantity})
        }

        data.append("movies", JSON.stringify(movies));
        data.append("date", String(new Date().getTime()));

        await fetch('http://' + BASE_URL + '/Fablix/api/sale/checkout', {
            method: "POST",
            body: data
        }).then(response => {
            console.log(response);
            return response.json()
        }).then(json => {
            // set receipt
            this.setState({
                receiptIds: json.data.sids,
                cart: []
            });
            this.homeRouter.current.context.router.history.push('/');
            this.homeRouter.current.context.router.history.push('/checkout');
        }).catch(error => createFailureToast("It seems like a network problem has occurred"));
    };

    redirectTo = (page) => {
        if (page === '') {
            this.homeRouter.current.context.router.history.push('/');
        } else {
            this.homeRouter.current.context.router.history.push('/');
            this.homeRouter.current.context.router.history.push('/' + page);
        }
    };

    toggleSearch = (e) => {
        this.setState({
            showSearchBar: !this.state.showSearchBar,
        })
    };

    render() {
        let homePageProps = {
            movieLetter: this.state.movieLetter,
            genreList: this.state.genreList,
            top20Movies: this.state.top20Movies,

            toggleModal: this.toggleModal,
            requestMovies: this.requestMovies,
        };

        let moviePageProps = {
            user: this.state.user,

            requestUrl: this.state.requestUrl,

            movies: this.state.movies,
            searchDescription: this.state.searchDescription,
            curPage: this.state.curPage,
            maxRecords: this.state.maxRecords,

            sortBy: this.state.sortBy,

            cart: this.state.cart,

            toggleModal: this.toggleModal,
            requestMovies: this.requestMovies,
            updateShoppingCart: this.updateShoppingCart,
        };

        let checkoutPageProps = {
            user: this.state.user,
            cart: this.state.cart,
            receiptIds: this.state.receiptIds,

            toggleModal: this.toggleModal,
            updateShoppingCart: this.updateShoppingCart,
        };

        return (
            <Router>
                <React.Fragment>
                    {this.state.showSearchBar &&
                    <Row tag='div' className='top-searchBar fixed-top'>
                        <Col xs='12' sm='12' md='2' lg='1' xl='1' className='text-center'>
                            <i className="fas fa-times fa-2x align-middle" onClick={this.toggleSearch}/>
                        </Col>
                        <Col xs='12' sm='12' md='9' lg='10' xl='10' className='align-middle'>
                            <SearchBar toggleModal={this.toggleModal} requestMoviesByTitle={this.requestMoviesByTitle}/>
                        </Col>
                    </Row>}
                    <div className="grid-container">
                        <header className="grid-header text-left marquee">
                            <span>Welcome to Fablix</span>
                        </header>
                        <section className="gird-navigator">
                            <div className="navigator-container">
                                <div className="navigator-login text-center">
                                    <User user={this.state.user} toggleModal={this.toggleModal}
                                          updateUser={this.updateUser}/>
                                </div>
                                <div className="navigator-nav text-center">
                                    <Link to="/"><i className="fas fa-home"/>&nbsp;Home</Link>&nbsp;&nbsp;&nbsp;&nbsp;
                                    <Link to="/search"><i
                                        className="fas fa-search"/>&nbsp;Search</Link>&nbsp;&nbsp;&nbsp;&nbsp;
                                    <Link to="/checkout"><i className="fas fa-cash-register"/>&nbsp;Checkout</Link>
                                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                                    <Button color='link' onClick={this.toggleSearch}><i className="fas fa-search-plus"/> Advance</Button>

                                </div>
                            </div>
                        </section>

                        <Route ref={this.homeRouter} exact path="/"
                               render={(props) => <HomePage {...props} {...homePageProps}/>}/>
                        <Route path="/search"
                               render={(props) => <SearchPage {...props} {...moviePageProps}/>}/>
                        <Route path="/checkout"
                               render={(props) => <CheckoutPage {...props} {...checkoutPageProps}/>}/>

                        <AppModal show={this.state.modalShow} type={this.state.modalType} data={this.state.modalData}
                                  close={this.closeModal} toggleModal={this.toggleModal} updateUser={this.updateUser}
                                  updateShoppingCart={this.updateShoppingCart}
                                  checkoutShoppingCart={this.checkoutShoppingCart} receiptIds={this.state.receiptIds}
                                  redirectTo={this.redirectTo}/>
                    </div>
                </React.Fragment>
            </Router>
        );
    }
}

function findIndexById(cart, id) {
    for (let i = 0; i < cart.length; i++) {
        if (cart[i].id === id) {
            return i;
        }
    }
    return -1;
}

export default App;
