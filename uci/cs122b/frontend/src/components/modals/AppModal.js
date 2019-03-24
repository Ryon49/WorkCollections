import React from 'react';
import {ModalType} from "../../config";
import {LoginModal, MovieModal, StarModal, ReceiptModal, ResultModal} from '.'

class AppModal extends React.Component {
    constructor(props) {
        super(props);

        this.createModal = this.createModal.bind(this);
    }

    createModal = () => {
        if (this.props.type === ModalType.MOVIE) {
            return <MovieModal {...this.props} />
        } else if (this.props.type === ModalType.STAR) {
            return <StarModal {...this.props} />
        } else if (this.props.type === ModalType.LOGIN) {
            return <LoginModal {...this.props} isCustomer={true} />
        } else if (this.props.type === ModalType.RECEIPT) {
            return <ReceiptModal {...this.props} />
        } else if (this.props.type === ModalType.EMPLOYEE) {
            return <LoginModal {...this.props} isCustomer={false} />
        } else if (this.props.type === ModalType.RESULT) {
            return <ResultModal {...this.props} />
        }
    };

    render() {
        return (
            <React.Fragment>
                {this.createModal()}
            </React.Fragment>
        );
    }
}

export default AppModal;
