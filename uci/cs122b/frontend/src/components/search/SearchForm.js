import React from 'react'
import './SearchForm.css'
import {Button, Form, Input, InputGroup, InputGroupAddon} from "reactstrap";
import {BASE_URL} from "../../config";

class SearchForm extends React.Component {

    constructor(props) {
        super(props);

        this.resetForm = this.resetForm.bind(this);
        this.onSubmit = this.onSubmit.bind(this);

        this.title = React.createRef();
        this.director = React.createRef();
        this.star = React.createRef();
        this.year = React.createRef();

        this.state = {
            activateReset: false,
        }
    }

    resetForm(e) {
        e.preventDefault();
        this.title.current.value = '';
        this.director.current.value = '';
        this.star.current.value = '';
        this.year.current.value = 0;
    }

    onSubmit(e) {
        e.preventDefault();

        let params = {};
        let title = this.title.current.value;
        if (title !== '') {
            params.title = title
        }
        let year = this.year.current.value;
        if (year !== '0') {
            params.year = year;
        }
        let director = this.director.current.value;
        if (director !== '') {
            params.director = director;
        }
        let star = this.star.current.value;
        if (star !== '') {
            params.star = star;
        }

        params.pageNum = 1;
        let requestUrl = 'http://' + BASE_URL + '/Fablix/api/movie/find';

        this.props.requestMovies(requestUrl, params, false, true)

        if (Object.keys(params).length > 0) {
            this.setState({
                activateReset: true,
            })
        }
    }

    render() {
        return <Form className='search-form'>
            Search Form:
            <InputGroup>
                <InputGroupAddon addonType="prepend">Title</InputGroupAddon>
                <Input placeholder="title" innerRef={this.title}/>
            </InputGroup>
            <InputGroup>
                <InputGroupAddon addonType="prepend">Year</InputGroupAddon>
                <Input type='select' innerRef={this.year}>
                    <option value={0}>-------Not Selected-------</option>
                    {years.map((y, idx) => <option key={idx}>{y}</option>)}
                </Input>
            </InputGroup>
            <InputGroup>
                <InputGroupAddon addonType="prepend">Director</InputGroupAddon>
                <Input placeholder="director" innerRef={this.director}/>
            </InputGroup>
            <InputGroup>
                <InputGroupAddon addonType="prepend">Star</InputGroupAddon>
                <Input placeholder="star name" innerRef={this.star}/>
            </InputGroup>

            <div className='text-right'>
                <Button size='sm' color="secondary" onClick={this.resetForm}>Clear</Button>
                {' ' + !this.state.activateReset ? '' : <Button size='sm' color="secondary" onClick={this.resetForm}>Clear</Button>}
                &nbsp;<Button size='sm' color="primary" onClick={this.onSubmit}>Search</Button>
            </div>
        </Form>
    }
}

let years = [...Array(19).keys()].reverse().map(x => x + 2001);

export default SearchForm;

