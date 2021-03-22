import axios from "axios";
function get_image(imageId) {
    var imageUrl = "/images/" + imageId;
    return axios.get(imageUrl, { responseType: "blob" });
}

function get_images() {
    return axios.get("images");
}

export default {
    get_image, get_images
}