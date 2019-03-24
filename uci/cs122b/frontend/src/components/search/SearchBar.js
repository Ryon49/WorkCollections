import React from 'react'
import Select, {Creatable} from "react-select";
import {createFailureToast, ModalType, BASE_URL, REQUEST_INTERVAL} from "../../config";
import {Button, Col, Row} from "reactstrap";

class SearchBar extends React.Component {
    constructor(props) {
        super(props);
        this.handleInputChange = this.handleInputChange.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.fetchOptions = this.fetchOptions.bind(this);
        this._onKeyDown = this._onKeyDown.bind(this);
        this.resetSignal = this.resetSignal.bind(this);
        this._onSearchClick = this._onSearchClick.bind(this);
        this.selectRef = React.createRef();

        this.state = {
            inputValue: "",
            options: [],
            optionsCache: {},
            searchValue: "",
            menuIsOpen: false,
            allowSearch: true,
            selectedOption: undefined
        };
        this.intervalNum = setInterval(this.resetSignal, REQUEST_INTERVAL)
    }

    componentWillUnmount() {
        clearInterval(this.intervalNum)
    }

    resetSignal() {
        this.setState({
            allowSearch: true
        })
    }

    handleInputChange(inputValue, action) {
        if (action.action === 'menu-close' || action.action === 'input-blur') {
            if (this.state.inputValue === '') {
                this.setState({
                    inputValue: "",
                })
            }
            return;
        }

        this.setState({
            inputValue: inputValue,
            menuIsOpen: false,
        });
        let trim = inputValue.trim();
        if (trim === this.state.searchValue) {
            return
        }
        if (inputValue !== '' && trim.length >= 3) {
            let options = this.state.optionsCache[trim];
            if (options === undefined) {
                let allowRequest = false;
                if (this.state.allowSearch) {
                    this.setState({
                        allowSearch: false
                    });
                    allowRequest = true
                }

                if (allowRequest === true) {
                    this.fetchOptions(trim)
                } else {
                    this.setState({
                        menuIsOpen: true
                    })
                }
                // debounced(this.fetchOptions, 1000)(trim);
            } else {
                console.log("get results from cache");
                console.log(options);
                this.setState({
                    options: options,
                    searchValue: trim,
                    menuIsOpen: true,

                });
            }
        }
    }

    fetchOptions = (title) => {
        let data = new FormData();
        data.append('title', title);
        data.append('maxRecords', '10');
        console.log("autocomplete initiated");
        console.log("sending async request to backend java dispatcher");
        fetch('http://' + BASE_URL + '/Fablix/api/movie/searchTitle', {
            method: "POST",
            body: data
        }).then(response => {
            return response.json()
        }).then(json => {
            let options = json.data.ids;
            let cache = this.state.optionsCache;

            console.log("lookup successful");
            console.log(options);

            if (Object.keys(cache).length < 10) {
                cache[title] = options
            } else {
                console.log("cache exceed capacity, clean up cache");
                cache = {title: options};
            }

            this.setState({
                optionsCache: cache,
                options: options,
                searchValue: options,
                menuIsOpen: true,
            })
        }).catch(error => createFailureToast("It seems like a network problem has occurred"));
    };

    handleChange = (selectedOption) => {
        if (selectedOption !== null) {
            // console.log(`Option selected:`, selectedOption);
            this.setState({
                inputValue: selectedOption.label
            });
            console.log(`you select '${selectedOption.label}' with ID ${selectedOption.value}`);
            this.props.toggleModal(ModalType.MOVIE, {id: selectedOption.value}, false, true);
        }
    };

    _onKeyDown(e) {
        if (e.keyCode === 13) {
            if (this.state.useSuggestion !== undefined) {
                this.handleChange(this.state.useSuggestion)
            } else {
                e.preventDefault();
                this.props.requestMoviesByTitle(this.state.inputValue);
                this.setState({
                    menuIsOpen: false,
                })
            }
        } else if (e.keyCode === 38 || e.keyCode === 40) {
            if (this.state.menuIsOpen === true && this.state.options.length > 0) {
                window.setTimeout(() => {
                    let focusedOption = this.selectRef.current.select.state.focusedOption;
                    this.setState({
                        inputValue: focusedOption.label,
                        useSuggestion: focusedOption
                    });
                }, 0);
            }
        } else {
            this.setState({
                useSuggestion: undefined
            })
        }
    }

    _onSearchClick(e) {
        this.props.requestMoviesByTitle(this.state.inputValue);
    }

    render() {
        const {inputValue, options} = this.state;
        return (
            <Row>
                <Col xs='12' sm='12' md='10' lg='10' xl='10'>
                    <Select
                        autofocus
                        ref={this.selectRef}
                        inputValue={inputValue}
                        placeholder={"Search Movie"}
                        filterOption={options => options}
                        name="movie_search"
                        isClearable={true}
                        options={options}
                        onChange={this.handleChange}
                        onInputChange={this.handleInputChange}
                        onKeyUp={() => console.log(this.selectRef.current.select.state.focusedOption)}
                        onKeyDown={this._onKeyDown}
                        menuIsOpen={this.state.menuIsOpen}
                        onBlur={() => this.setState({menuIsOpen: false})}
                    />
                </Col>
                <Col xs='12' sm='12' md='2' lg='2' xl='2'>
                    <Button onClick={this._onSearchClick}>Search</Button>
                </Col>
            </Row>
        );
    }
}


export default SearchBar
