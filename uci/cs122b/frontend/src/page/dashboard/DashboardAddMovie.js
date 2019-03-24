import React from 'react'
import {Button, Form, FormGroup, Input, InputGroup} from "reactstrap";
import {BASE_URL, createFailureToast} from "../../config";
import './DashboardPage.css'

class DashboardAddMovie extends React.Component {

    constructor(props) {
        super(props);

        this.onSubmit = this.onSubmit.bind(this);
        this.addStar = this.addStar.bind(this);

        this.state = {
            recordCnt: 0,
            titlesError: [],
            directorGenresError: [],
            starsError: [],
        }
    }

    onSubmit = (e) => {
        e.preventDefault();
        if (e.stopPropagation)
            e.stopPropagation();

        let titlesError = [];
        let directorGenresError = [];
        let starsError = [];
        let hasError = false;

        let titles = [];
        let years = [];
        let directors = [];
        let genres = [];
        let starNames = [];
        let starYears = [];
        if (this.state.recordCnt === 1) {
            let title = e.target.title.value;
            if (title === '') {
                titlesError[0] = true;
                hasError = true;
            } else {
                titles.push(title);
            }

            let year = Number(e.target.year.value);
            if (e.target.year.value === '' || isNaN(year)) {
                titlesError[0] = true;
                hasError = true;
            } else {
                years.push(year);
            }

            let director = e.target.director.value;
            if (director === '') {
                directorGenresError[0] = true;
                hasError = true
            } else {
                directors.push(director)
            }

            let genre = e.target.genre.value;
            if (genre === '') {
                directorGenresError[0] = true;
                hasError = true;
            } else {
                genres.push(genre)
            }

            let starName = e.target.starName.value;
            if (starName === '') {
                starsError[0] = true;
                hasError = true;
            } else {
                starNames.push(starName)
            }

            let starYear = Number(e.target.starYear.value);
            if (isNaN(starYear)) {
                starsError[0] = true;
                hasError = true;
            } else {
                starYears.push(starYear);
            }
        } else {
            for (let i = 0; i < this.state.recordCnt; i++) {
                let title = e.target.title[i].value;
                if (title === '') {
                    titlesError[i] = true;
                    hasError = true;
                } else {
                    titles.push(title);
                }

                let year = Number(e.target.year[i].value);
                if (e.target.year[i].value === '' || isNaN(year)) {
                    titlesError[i] = true;
                    hasError = true;
                } else {
                    years.push(year);
                }

                let director = e.target.director[i].value;
                if (director === '') {
                    directorGenresError[i] = true;
                    hasError = true
                } else {
                    directors.push(director)
                }

                let genre = e.target.genre[i].value;
                if (genre === '') {
                    directorGenresError[i] = true;
                    hasError = true;
                } else {
                    genres.push(genre)
                }

                let starName = e.target.starName[i].value;
                if (starName === '') {
                    starsError[i] = true;
                    hasError = true;
                } else {
                    starNames.push(starName)
                }

                let starYear = Number(e.target.starYear[i].value);
                if (isNaN(starYear)) {
                    starsError[i] = true;
                    hasError = true;
                } else {
                    starYears.push(starYear);
                }
            }
        }

        if (hasError) {
            this.setState({
                titlesError: titlesError,
                directorGenresError: directorGenresError,
                starsError: starsError,
            })
        } else {
            // submit form here
            let data = new FormData();
            data.append('titles', titles.join(","));
            data.append('years', years.join(","));
            data.append('directors', directors.join(","));
            data.append('genres', genres.join(","));
            data.append('starNames', starNames.join(","));
            data.append('starYears', starYears.join(","));

            fetch('http://' + BASE_URL + '/Fablix/api/movie/add',
                {
                    method: "POST",
                    body: data
                })
                .then(response => response.json())
                .then(json => {
                    console.log(json);
                    if (json.success === true) {
                        let ids = json.data.ids;
                        let data = {};
                        data['type'] = 'Movie';
                        data['ids'] = ids;
                        this.props.toggleModal(undefined, data, true, true)
                        this.setState({
                            recordCnt: 0
                        })
                    } else {
                        createFailureToast("It seems like a problem has occurred")
                    }
                }).catch(error => createFailureToast("It seems like a network problem has occurred"));
            this.setState({
                titlesError: [],
                directorGenresError: [],
                starsError: []
            })
        }
    };

    addStar = () => {
        this.setState({
            recordCnt: this.state.recordCnt + 1
        })
    };

    render() {
        return <React.Fragment>
            <Form method="POST" onSubmit={this.onSubmit} className='dashboard-form'>
                <Button color='link' onClick={this.addStar} style={{marginBottom: '10px'}}>
                    <i className="fas fa-plus-circle"/> New Entry</Button>
                {this.state.recordCnt > 0 && [...Array(this.state.recordCnt).keys()].map(idx =>
                    <React.Fragment key={idx}>
                        <FormGroup>
                            <InputGroup>
                                <div className="input-group-prepend">
                                    <span className="input-group-text">{idx + 1}. </span>
                                </div>
                                <Input type="text" name="title" placeholder="Movie title"/>

                                <div className="input-group-prepend">
                                    <span className="input-group-text"><i className="far fa-calendar-alt"/></span>
                                </div>
                                <Input type="text" name="year" placeholder="Year"/>
                            </InputGroup>
                            {this.state.titlesError[idx] === true ? <div className="dashboard-error">
                                &nbsp;&nbsp;&nbsp;&nbsp;Empty movie title or invalid year information</div> : ''}
                        </FormGroup>
                        <FormGroup>
                            <InputGroup>
                                <div className="input-group-prepend">
                                    <span className="input-group-text"><i className="fas fa-video"/></span>
                                </div>
                                <Input type="text" name="director" placeholder="Director"/>
                                <div className="input-group-prepend">
                                    <span className="input-group-text"><i className="fas fa-hdd"/></span>
                                </div>
                                <Input type="text" name="genre" placeholder="Genre (Multiple genres split by comma)"/>
                            </InputGroup>
                            {this.state.directorGenresError[idx] === true ? <div className="dashboard-error">
                                &nbsp;&nbsp;&nbsp;&nbsp;Empty director or genre information</div> : ''}
                        </FormGroup>
                        <FormGroup>
                            <InputGroup>
                                <div className="input-group-prepend">
                                    <span className="input-group-text"><i className="fas fa-address-card"/></span>
                                </div>
                                <Input type="text" name="starName" placeholder="Star Name"/>
                                <div className="input-group-prepend">
                                    <span className="input-group-text"><i className="fas fa-male"/></span>
                                </div>
                                <Input type="text" name="starYear" placeholder="Star Birth Year (Empty if choose existed star)"/>
                            </InputGroup>
                            {this.state.starsError[idx] === true ? <div className="dashboard-error">
                                &nbsp;&nbsp;&nbsp;&nbsp;Empty star name or invalid star birth year</div> : ''}
                        </FormGroup>
                        <br/>
                    </React.Fragment>)}
                {this.state.recordCnt > 0 && <Button color='primary'>Add new movies</Button>}
            </Form>
        </React.Fragment>
    }
}

export default DashboardAddMovie;
