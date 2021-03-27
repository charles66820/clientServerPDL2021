import axios from "axios";
function get_image(imageId) {
    var imageUrl = "/images/" + imageId;
    return axios.get(imageUrl, { responseType: "blob" });
}

function get_images() {
    return axios.get("images");
}

function get_imageData(imageId) {
    var imageUrl = "/images/" + imageId;
    return axios.get(imageUrl, {headers: {'accept': 'application/json'}});
}

function delete_image(imageId) {
    var imageUrl = "/images/" + imageId;
    return axios.delete(imageUrl);
}

export default {
    get_image, get_images, get_imageData, delete_image
}