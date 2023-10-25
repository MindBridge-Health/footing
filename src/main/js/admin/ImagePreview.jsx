import React, {useEffect, useState} from 'react';
import { Carousel } from 'react-responsive-carousel';
import 'react-responsive-carousel/lib/styles/carousel.min.css';
import {Loading} from "../Loading";
import {Error} from "../Error";

function ImagePreview({ storytellerId }) {
    const [imageUrls, setImageUrls] = useState([]);
    const [loadingImages, setLoadingImages] = useState(true)
    const [errorImages, setErrorImages] = useState(null)

    useEffect(() => {
        async function fetchImageUrls() {
            try {
                console.log('fetching');
                // Fetch image URLs from your backend API
                const response = await fetch(`/api/v1/media/storytellers/${storytellerId}`);
                const data = await response.json();

                // Extract the 'location' field from each object to get image URLs
                const urls = data.map(item => `${item.location}`);
                setImageUrls(urls);
                setLoadingImages(false);
                console.log('fetched');
            } catch (error) {
                setErrorImages(error);
                console.error('Error fetching image URLs:', error);
            }
        }

        fetchImageUrls();
        console.log('useEffect ended');
    }, []);

    if (loadingImages) {
        return <Loading/>;
    }

    if (errorImages) {
        return <Error message={errorImages.message}/>;
    }

    return (
        <div>
            <h1>Uploaded Images</h1>
            <Carousel>
                {imageUrls.map((imageUrl, index) => (
                    <img key={`Image_${index}`} src={imageUrl} alt={`Image ${index}`} />
                ))}
            </Carousel>
        </div>
    );
}

export default ImagePreview;
