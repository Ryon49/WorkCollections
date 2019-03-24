import React from 'react'

import './DashboardPage.css'
import {Nav, NavItem, NavLink, TabContent, Table, TabPane} from "reactstrap";

class DashboardScheme extends React.Component {

    constructor(props) {
        super(props);

        this.toggle = this.toggle.bind(this);

        let schemes = sessionStorage.getItem("schemes");

        if (schemes !== null) {
            schemes = JSON.parse(schemes)
        } else {
            schemes = undefined;
        }
        this.state = {
            activeTab: '0',
            schemes: schemes
        };
    }

    componentWillReceiveProps(nextProps) {
        if (this.state.schemes === undefined && nextProps.schemes !== undefined) {
            this.setState({
                schemes: nextProps.schemes
            });
            sessionStorage.setItem("schemes", JSON.stringify(nextProps.schemes))
        }
    }

    toggle(tab) {
        if (this.state.activeTab !== tab) {
            this.setState({
                activeTab: tab
            });
        }
    }

    render() {
        return <React.Fragment>
            <Nav tabs justified>
                {!this.state.schemes ? '' : this.state.schemes.map((table, idx) => {
                    return <NavItem key={idx}>
                        <NavLink onClick={() => this.toggle(String(idx))}>
                            <b>{table.tableName}</b>
                        </NavLink>
                    </NavItem>
                })}
            </Nav>
            <TabContent activeTab={this.state.activeTab}>
                {!this.state.schemes ? '' : this.state.schemes.map((table, idx) => {
                    return <TabPane key={idx} tabId={String(idx)}>
                        {Present(table)}
                    </TabPane>
                })}
            </TabContent>
        </React.Fragment>
    }
}

function Present(table) {
    return <div className='scheme-table'>
        <Table striped bordered>
            <thead>
            <tr>
                <th>Column Name</th>
                <th>Column Type</th>
                <th>Is Primary</th>
            </tr>
            </thead>
            <tbody>
            {table.columns.map((c, idx) => <tr key={idx}>
                <td>{c.name}</td>
                <td>{c.type}</td>
                <td>{c.primary ? "Primary Key" : ""}</td>
            </tr>)}
            </tbody>
        </Table>
    </div>
}

export default DashboardScheme;
