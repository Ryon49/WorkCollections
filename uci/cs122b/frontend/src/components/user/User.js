import React from 'react'
import {ModalType} from "../../config";
import {Button} from "reactstrap";

class User extends React.Component {

    render() {
        return this.props.user ?
            <UserInfo {...this.props}/> :
            <LoginInterface toggleModal={this.props.toggleModal}/>
    }
}

function UserInfo(props) {
    let hours = new Date().getHours();
    let msg = '';
    if (hours < 12) {
        msg = 'morning'
    } else if (hours < 18) {
        msg = 'afternoon'
    } else {
        msg = 'evening'
    }
    return <React.Fragment>
        <span>Good {msg}, {props.user.firstName} </span>
        <Button size='sm' onClick={() => props.updateUser(undefined)}>Logout</Button>
    </React.Fragment>
}

function LoginInterface(props) {
    return <div> <span>Please, </span>
        <Button size='sm' onClick={() => props.toggleModal(ModalType.LOGIN, "", true, false)}>Login</Button>
    </div>
}


export default User;
