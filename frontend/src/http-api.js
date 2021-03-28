import axios from "axios";

function get_image(imageId) {
    let imageUrl = "/images/" + imageId;
    return axios.get(imageUrl, { headers: {accept: "image/*,*/*" }, responseType: "blob" });
}

function get_images() {
    return axios.get("images");
}

function get_imageData(imageId) {
    let imageUrl = "/images/" + imageId;
    return axios.get(imageUrl, { headers: { 'accept': 'application/json' } });
}

function get_image_with_algo(imageId, queryString) {
    let imageUrl = "/images/" + imageId + "?" + queryString;
    return axios.get(imageUrl, { headers: {accept: "image/*,*/*" }, responseType: "blob" });
}

function delete_image(imageId) {
    let imageUrl = "/images/" + imageId;
    return axios.delete(imageUrl);
}

function get_algos() {
    let algorithmsUrl = "/algorithms/";
    return axios.get(algorithmsUrl, {header: {'accept': 'application/json'}});
}

function post_image(image) {
    let postUrl = "/images/";
    return axios.post(postUrl, {image});
}

export default {
    get_image, get_images, get_imageData, get_image_with_algo, delete_image, get_algos, post_image
}