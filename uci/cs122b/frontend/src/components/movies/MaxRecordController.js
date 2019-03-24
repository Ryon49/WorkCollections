import React from 'react'
import {Col, Input, Label, Row} from "reactstrap";

class MaxRecordController extends React.Component {

    constructor(props) {
        super(props);

        this.onChange = this.onChange.bind(this);
        this.state = {
            maxRecords: this.props.maxRecords
        }
    }

    componentWillReceiveProps(nextProps) {
        if (this.state.maxRecords !== nextProps.maxRecords) {
            this.setState({
                maxRecords: nextProps.maxRecords
            })
        }
    }

    onChange(e) {
        let x = {maxRecords: e.target.value};
        this.props.requestMovies(undefined, x, true, true)
    }

    render() {
        return <Row>
            <Col className='nopadding text-center' xs='12' sm='12' md='6' lg='6' xl='6'>
                <Label for="maxRecordController">Record displayed</Label>
            </Col>
            <Col className='nopadding' xs='12' sm='12' md='3' lg='3' xl='3'>
                    <Input type="select" name="maxRecords" id="maxRecordController" bsSize='sm'
                           onChange={this.onChange} value={this.state.maxRecords}>
                        {options.map((o, key) =>
                            <option key={key} value={o}>{o}</option>
                        )}
                    </Input>
            </Col>
        </Row>
    }
}

const options = [10, 25, 50, 100];

export default MaxRecordController;
