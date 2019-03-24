import React from "react";

function MovieRating(props) {
    let ratingObj = props.rating;

    return <React.Fragment >
        {ratingObj === null ? 'No rating available' : createRatings(ratingObj.rating)}
    </React.Fragment>
}

function createRatings(rating) {
    let stars = [];
    let r = Number(rating);

    if (r % 1 >= 0.65) {
        r = Math.ceil(r);
    } else if (r % 1 <= 0.2) {
        r = Math.floor(r);
    }

    switch (Math.floor(r)) {
        case 0:
            stars.push(<i key={0} className='far fa-star'/>);
            stars.push(<i key={1} className='far fa-star'/>);
            stars.push(<i key={2} className='far fa-star'/>);
            stars.push(<i key={3} className='far fa-star'/>);
            stars.push(<i key={4} className='far fa-star'/>);
            break;
        case 1:
            stars.push(<i key={0} className="fa fa-star-half-alt"/>);
            stars.push(<i key={1} className='far fa-star'/>);
            stars.push(<i key={2} className='far fa-star'/>);
            stars.push(<i key={3} className='far fa-star'/>);
            stars.push(<i key={4} className='far fa-star'/>);
            break;
        case 2:
            stars.push(<i key={0} className='fa fa-star'/>);
            stars.push(<i key={1} className='far fa-star'/>);
            stars.push(<i key={2} className='far fa-star'/>);
            stars.push(<i key={3} className='far fa-star'/>);
            stars.push(<i key={4} className='far fa-star'/>);
            break;
        case 3:
            stars.push(<i key={0} className='fa fa-star'/>);
            stars.push(<i key={1} className="fa fa-star-half-alt"/>);
            stars.push(<i key={2} className='far fa-star'/>);
            stars.push(<i key={3} className='far fa-star'/>);
            stars.push(<i key={4} className='far fa-star'/>);
            break;
        case 4:
            stars.push(<i key={0} className='fa fa-star'/>);
            stars.push(<i key={1} className='fa fa-star'/>);
            stars.push(<i key={2} className='far fa-star'/>);
            stars.push(<i key={3} className='far fa-star'/>);
            stars.push(<i key={4} className='far fa-star'/>);
            break;
        case 5:
            stars.push(<i key={0} className='fa fa-star'/>);
            stars.push(<i key={1} className='fa fa-star'/>);
            stars.push(<i key={2} className="fa fa-star-half-alt"/>);
            stars.push(<i key={3} className='far fa-star'/>);
            stars.push(<i key={4} className='far fa-star'/>);
            break;
        case 6:
            stars.push(<i key={0} className='fa fa-star'/>);
            stars.push(<i key={1} className='fa fa-star'/>);
            stars.push(<i key={2} className='fa fa-star'/>);
            stars.push(<i key={3} className='far fa-star'/>);
            stars.push(<i key={4} className='far fa-star'/>);
            break;
        case 7:
            stars.push(<i key={0} className='fa fa-star'/>);
            stars.push(<i key={1} className='fa fa-star'/>);
            stars.push(<i key={2} className='fa fa-star'/>);
            stars.push(<i key={3} className="fa fa-star-half-alt"/>);
            stars.push(<i key={4} className='far fa-star'/>);
            break;
        case 8:
            stars.push(<i key={0} className='fa fa-star'/>);
            stars.push(<i key={1} className='fa fa-star'/>);
            stars.push(<i key={2} className='fa fa-star'/>);
            stars.push(<i key={3} className='fa fa-star'/>);
            stars.push(<i key={4} className='far fa-star'/>);
            break;
        case 9:
            stars.push(<i key={0} className='fa fa-star'/>);
            stars.push(<i key={1} className='fa fa-star'/>);
            stars.push(<i key={2} className='fa fa-star'/>);
            stars.push(<i key={3} className='fa fa-star'/>);
            stars.push(<i key={4} className="fa fa-star-half-alt"/>);
            break;
        case 10:
            stars.push(<i key={0} className='fa fa-star'/>);
            stars.push(<i key={1} className='fa fa-star'/>);
            stars.push(<i key={2} className='fa fa-star'/>);
            stars.push(<i key={3} className='fa fa-star'/>);
            stars.push(<i key={4} className='fa fa-star'/>);
            break;
        default:
            break;
    }

    return <span>
        {stars}
        <span>&nbsp;({rating})</span>
    </span>
}

export default MovieRating;
