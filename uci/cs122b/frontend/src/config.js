import {toast} from "react-toastify";

// export const BASE_URL = '35.236.54.118/';
export const REQUEST_INTERVAL = 300;
export const BASE_URL = window.location.host.split(":")[0] + ":8080";

export const ModalType = Object.freeze({
    MOVIE:   'movie',
    STAR:  'star',
    LOGIN: 'login',
    NONE: 'none',
    RECEIPT: 'receipt',
    EMPLOYEE: 'employee',
    RESULT: 'result',
});

let existOneErrorToast = false;
let existOneSuccessToast = false;
let existOneInfoToast = false;

export function createSuccessToast(msg, duration) {
    if (existOneSuccessToast) {
        return;
    }
    if (duration === undefined) {
        duration = 1000;
    }

    toast.success(msg, {
        position: "bottom-right",
        autoClose: duration,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: false,
        draggable: false,
        onOpen: () => {
            existOneSuccessToast = true
        },
        onClose: () => {
            existOneSuccessToast = false
        },
    });
}

export function createInfoToast(msg, duration) {
    if (existOneInfoToast) {
        return;
    }
    if (duration === undefined) {
        duration = 1000;
    }

    toast.info(msg, {
        position: "bottom-right",
        autoClose: duration,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: false,
        draggable: false,
        onOpen: () => {
            existOneInfoToast = true
        },
        onClose: () => {
            existOneInfoToast = false
        },
    });
}


export function createFailureToast(message) {
    if (existOneErrorToast) {
        return;
    }
    toast.error(message, {
        position: "top-center",
        autoClose: 1500,
        hideProgressBar: false,
        closeOnClick: true,
        pauseOnHover: false,
        draggable: false,
        onOpen: () => {
            existOneErrorToast = true
        },
        onClose: () => {
            existOneErrorToast = false
        },
    });
}

// Credit David Walsh (https://davidwalsh.name/javascript-debounce-function) concept
// Credit blog (https://codeburst.io/throttling-and-debouncing-in-javascript-646d076d0a44) actual code
// export function debounced(fn, delay) {
//     let timerId;
//     return function (...args) {
//         if (timerId) {
//             return
//         }
//         timerId = setTimeout(() => {
//             fn(...args);
//             timerId = null;
//         }, delay);
//     }
// }
