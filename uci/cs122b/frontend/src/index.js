import React from 'react';
import ReactDOM from 'react-dom';
import 'bootstrap/dist/css/bootstrap.min.css';
import './index.css';
// import App from './App';
import App from './App';
import * as serviceWorker from './serviceWorker';

import 'react-toastify/dist/ReactToastify.css';

import {ToastContainer} from "react-toastify";
import {loadReCaptcha} from "react-recaptcha-google";
import {BrowserRouter, Route} from "react-router-dom";
import {DashboardPage} from "./page/";

ReactDOM.render(<BrowserRouter>
    <React.Fragment>
        <link rel="stylesheet" href="https://use.fontawesome.com/releases/v5.6.3/css/all.css"
              integrity="sha384-UHRtZLI+pbxtHCWp1t77Bi1L4ZtiqrqD80Kn4Z8NTSRyMA2Fd33n5dQ8lWUE00s/"
              crossOrigin="anonymous"/>
        {loadReCaptcha()}
        {/*<App/>*/}
        <Route exact path="/" render={() => <App/>}/>
        <Route exact path="/search" render={() => <App/>}/>
        <Route exact path="/checkout" render={() => <App/>}/>
        <Route exact path="/_dashboard" render={() => <DashboardPage/>}/>
        <ToastContainer />
    </React.Fragment>
</BrowserRouter>, document.getElementById('root'));

// ReactDOM.render(<BasicExample/>, document.getElementById('root'));


// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
serviceWorker.register();
