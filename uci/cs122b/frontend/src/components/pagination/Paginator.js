import React from "react";
import {Pagination, PaginationItem, PaginationLink} from "reactstrap";
import './Paginator.css';

class Paginator extends React.Component {

    constructor(props) {
        super(props);
        this.onClick = this.onClick.bind(this);
    }

    onClick(e, requestUrl, pageNum) {
        e.preventDefault();
        this.props.requestMovies(requestUrl, {pageNum: pageNum}, true, true)
    }

    render() {
        let pageInfo = this.props.curPage;
        // let pages = generate(pageInfo);

        let pages = generate(this.props.curPage);
        return (
            <Pagination size='sm'>
                <PaginationItem disabled={pageInfo[0] <= 1}>
                    <PaginationLink href={this.props.url + "?pageNum=1"}
                                    onClick={(e) => this.onClick(e, this.props.url, 1)}>
                        {'««'}
                    </PaginationLink>
                </PaginationItem>

                <PaginationItem disabled={pageInfo[0] <= 1}>
                    <PaginationLink previous href={this.props.url + "?pageNum=" + String(pageInfo[0] - 1)}
                                    onClick={(e) => this.onClick(e, this.props.url, pageInfo[0] - 1)}/>
                </PaginationItem>

                {pages.map((pageNum, idx) =>
                    <PaginationItem key={idx} active={pageNum === pageInfo[0]}>
                        <PaginationLink href={this.props.url + "?pageNum=" + pageNum}
                                        onClick={(e) => this.onClick(e, this.props.url, pageNum)}>
                            {pageNum}
                        </PaginationLink>
                    </PaginationItem>)}

                <PaginationItem disabled={pageInfo[0] >= pageInfo[1]}>
                    <PaginationLink next href={this.props.url + "?pageNum=" + String(pageInfo[0] + 1)}
                                    onClick={(e) => this.onClick(e, this.props.url, pageInfo[0] + 1)}/>
                </PaginationItem>

                <PaginationItem disabled={pageInfo[0] >= pageInfo[1]}>
                    <PaginationLink href={this.props.url + "?pageNum=" + pageInfo[1]}
                                    onClick={(e) => this.onClick(e, this.props.url, pageInfo[1])}>
                        {'»»'}
                    </PaginationLink>
                </PaginationItem>
            </Pagination>
        );
    }
}


function generate(pageInfo) {
    let curPage = pageInfo[0];
    let totalPages = pageInfo[1];

    let pageList = [curPage];

    for (let c = 1; c <= 5; c++) {
        pageList = [curPage - c, ...pageList, curPage + c]
    }
    pageList = pageList.filter(x => x > 0);
    while (pageList.length <= 11) {
        pageList.push(pageList[pageList.length - 1] + 1)
    }
    pageList = pageList.filter(x => x <= totalPages);
    while (pageList.length <= 11) {
        pageList.unshift(pageList[0] - 1)
    }
    pageList = pageList.filter(x => x > 0);

    return pageList;
}


export default Paginator;
