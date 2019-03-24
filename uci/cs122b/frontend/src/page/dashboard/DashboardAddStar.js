import React from 'react'
import {Button, Form, FormGroup, Input, InputGroup} from "reactstrap";
import {BASE_URL, createFailureToast} from "../../config";
import './DashboardPage.css'

class DashboardAddStar extends React.Component {

    constructor(props) {
        super(props);

        this.onSubmit = this.onSubmit.bind(this);
        this.addStar = this.addStar.bind(this);

        this.state = {
            recordCnt: 0,
            whichError: []
        }
    }

    onSubmit = (e) => {
        e.preventDefault();
        if (e.stopPropagation)
            e.stopPropagation();

        let whichError = [];
        let hasError = false;

        let names = [];
        let years = [];
        if (this.state.recordCnt === 1) {
            let name = e.target.name.value;
            if (name === '') {
                whichError[0] = true;
                hasError = true;
            } else {
                names.push(name);
            }

            let year = Number(e.target.birthYear.value);
            if (isNaN(year)) {
                whichError[0] = true;
                hasError = true;
            } else {
                years.push(year);
            }
        } else {
            for (let i = 0; i < this.state.recordCnt; i++) {
                let name = e.target.name[i].value;
                if (name === '') {
                    whichError[i] = true;
                    hasError = true;
                } else {
                    names.push(name);
                }

                let year = Number(e.target.birthYear[i].value);
                if (isNaN(year)) {
                    whichError[i] = true;
                    hasError = true;
                } else {
                    years.push(year);
                }
            }
        }

        if (hasError) {
            this.setState({
                whichError: whichError
            })
        } else {
            // submit form here
            let data = new FormData();
            data.append('names', names.join(","));
            data.append('birthYears', years.join(","));

            fetch('http://' + BASE_URL + '/Fablix/api/star/add',
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
                        data['type'] = 'Star';
                        data['ids'] = ids;
                        this.props.toggleModal(undefined, data, true, true)
                        this.setState({
                            recordCnt: 0
                        })
                    } else {
                        createFailureToast("It seems like a problem has occurred")
                    }
                }).catch(error => createFailureToast("It seems like a problem has occurred"));
            this.setState({
                titlesError: []
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
                    <FormGroup key={idx}>
                        <InputGroup>
                            <div className="input-group-prepend">
                                <span className="input-group-text">{idx + 1}. </span>
                            </div>
                            <Input type="text" name="name" placeholder="Star Name"/>
                            <div className="input-group-prepend">
                                <span className="input-group-text"><i className="fas fa-calendar-times"/></span>
                            </div>
                            <Input type="text" name="birthYear" placeholder="Birth Year"/>
                        </InputGroup>
                        {this.state.whichError[idx] === true ? <div className="dashboard-error">
                            &nbsp;&nbsp;&nbsp;&nbsp;Empty star name or invalid birth year</div> : ''}
                    </FormGroup>)}
                {this.state.recordCnt > 0 && <Button color='primary'>Add new stars</Button>}
            </Form>
        </React.Fragment>
    }
}

export default DashboardAddStar;
