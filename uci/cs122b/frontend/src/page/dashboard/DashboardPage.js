import React from 'react'
import './DashboardPage.css'
import {AppModal} from "../../components";
import {BASE_URL, createFailureToast, ModalType} from "../../config";
import {Nav, NavItem, NavLink, TabContent, TabPane} from "reactstrap";
import {DashboardScheme, DashboardAddStar, DashboardAddMovie} from "./";

class DashboardPage extends React.Component {
    constructor(props) {
        super(props);

        this.updateUser = this.updateUser.bind(this);
        this.requestSchemes = this.requestSchemes.bind(this);
        this.logout = this.logout.bind(this);
        this.toggleModal = this.toggleModal.bind(this);
        this.closeModal = this.closeModal.bind(this);

        let employee = sessionStorage.getItem("employee");
        if (employee !== null) {
            employee = JSON.parse(employee)
        } else {
            employee = undefined
        }

        this.state = {
            modalShow: false,
            modalType: undefined,
            modalData: undefined,

            employee: employee,
            schemes: undefined
        }
    }


    componentDidMount() {
        if (this.state.employee === undefined) {
            this.setState({
                modalShow: true,
                modalType: ModalType.EMPLOYEE
            })
        }
    };


    updateUser = (employee) => {
        sessionStorage.setItem("employee", JSON.stringify(employee));
        this.setState({
            employee: employee,
            modalShow: false
        });
        if (this.state.schemes === undefined) {
            this.requestSchemes();
        }
    };

    requestSchemes = () => {
        fetch('http://' + BASE_URL + '/Fablix/api/basic/scheme')
            .then(response => response.json())
            .then(json => {
                this.setState({
                    schemes: json.data.tables
                })
            })
    };

    logout = () => {
        this.setState({
            employee: undefined,
            modalShow: true,
            modalType: ModalType.EMPLOYEE
        })
    };

    async toggleModal(type, data, complete, requireLogin) {
        if (requireLogin && this.state.employee === undefined) {
            createFailureToast("Please login first");
            return;
        }

        // close existing modal
        this.closeModal();

        this.setState({
            modalShow: true,
            modalType: ModalType.RESULT,
            modalData: data,
        })
    }

    closeModal() {
        if (this.state.modalShow === true) {
            this.setState({
                modalShow: false,
            })
        }
    };

    render() {
        return (
            <React.Fragment>
                {this.state.employee === undefined ? '' :
                    <React.Fragment>
                        <div className='fixed-bottom text-center'>
                            Login as <b>{this.state.employee.fullname}</b>&nbsp;&nbsp;
                            <span className='fake-button' onClick={this.logout}>Logout</span>
                        </div>
                        <Tabs schemes={this.state.schemes} toggleModal={this.toggleModal}/>
                    </React.Fragment>}
                <AppModal show={this.state.modalShow} type={this.state.modalType} data={this.state.modalData}
                          close={this.closeModal} toggleModal={this.toggleModal} updateUser={this.updateUser}/>
            </React.Fragment>
        )
    }
}

class Tabs extends React.Component {
    constructor(props) {
        super(props);

        this.toggle = this.toggle.bind(this);
        this.state = {
            activeTab: '1'
        };
    }

    toggle(tab) {
        if (this.state.activeTab !== tab) {
            this.setState({
                activeTab: tab
            });
        }
    }

    render() {
        // style={this.state.activeTab === 2 ? {...activeItemStyle} : ''}
        return <div>
            <Nav tabs justified>
                <NavItem className={this.state.activeTab === '1' ? 'navitem-active' : ''}>
                    <NavLink
                        onClick={() => {
                            this.toggle('1');
                        }}>
                        Show schemes
                    </NavLink>
                </NavItem>
                <NavItem className={this.state.activeTab === '2' ? 'navitem-active' : ''}>
                    <NavLink
                        onClick={() => {
                            this.toggle('2');
                        }}>
                        Add new star
                    </NavLink>
                </NavItem>
                <NavItem className={this.state.activeTab === '3' ? 'navitem-active' : ''}>
                    <NavLink
                        onClick={() => {
                            this.toggle('3');
                        }}>
                        Add new movie
                    </NavLink>
                </NavItem>
            </Nav>
            <TabContent activeTab={this.state.activeTab}>
                <TabPane tabId="1">
                    <DashboardScheme schemes={this.props.schemes}/>
                </TabPane>
                <TabPane tabId="2">
                    <DashboardAddStar toggleModal={this.props.toggleModal} close={this.props.close}/>
                </TabPane>
                <TabPane tabId="3">
                    <DashboardAddMovie toggleModal={this.props.toggleModal} close={this.props.close}/>
                </TabPane>
            </TabContent>
        </div>
    }
}

export default DashboardPage
