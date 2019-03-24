import React from 'react'
import {Col, Input, Label, Row} from "reactstrap";

class SortController extends React.Component {
    constructor(props) {
        super(props);

        this.onChange = this.onChange.bind(this);
        this.state = {
            sortBy: this.props.sortBy
        }
    }

    componentWillReceiveProps(nextProps) {
        if (this.state.sortBy !== nextProps.sortBy) {
            this.setState({
                sortBy: nextProps.sortBy
            })
        }
    }

    onChange(e) {
        let x = {sortBy: e.target.value, pageNum: 1};
        this.props.requestMovies(undefined, x, true, true)

    }
    render() {
        return <Row>
            <Col className='nopadding text-center' xs='12' sm='12' md='2' lg='2' xl='2'>
                <Label for="maxRecordController">Sort By</Label>
            </Col>
            <Col className='nopadding' xs='12' sm='12' md='4' lg='4' xl='4'>
                <Input type="select" name="maxRecords" id="maxRecordController" bsSize='sm'
                       onChange={this.onChange} value={this.state.sortBy}>
                    <option value={0}>Not Selected</option>
                    {options.map((o, key) =>
                        <option key={key} value={o.value}>{o.label}</option>
                    )}
                </Input>
            </Col>
        </Row>
    }
}

const options = [
    {label: 'Title: Ascending', value: 1},
    {label: 'Title: Descending', value: 2},
    {label: 'Rating: Ascending', value: 3},
    {label: 'Rating: Descending', value: 4},
];

export default SortController;
