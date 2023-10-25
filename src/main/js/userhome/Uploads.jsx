import React, {useEffect, useState} from 'react';
import { Carousel } from 'react-responsive-carousel';
import 'react-responsive-carousel/lib/styles/carousel.min.css';
import {Loading} from "../Loading";
import {Error} from "../Error";
import {useAuth0} from "@auth0/auth0-react";
import ImageUpload from "./ImageUpload";

function Uploads({ storytellerId }) {
    const { getAccessTokenSilently } = useAuth0();
    const [imageUrls, setImageUrls] = useState([]);
    const [loadingImages, setLoadingImages] = useState(true)
    const [errorImages, setErrorImages] = useState(null)

    useEffect(() => {
        async function fetchImageUrls() {
            try {
                // Fetch image URLs from your backend API
                 const token = await getAccessTokenSilently();
                    const response = await fetch(`/api/v1/media/storytellers/`,
                        {
                            headers: {
                                'Accept': 'application/json',
                                'Content-Type': 'application/json',
                                Authorization: `Bearer ${token}`,
                            }
                        });
                    const data = await response.json();

                    // Extract the 'location' field from each object to get image URLs
                    const urls = data.map(item => `${item.location}`);
                    setImageUrls(urls);
                    setLoadingImages(false);

            } catch (error) {
                setErrorImages(error);
                console.error('Error fetching image URLs:', error);
            }
        }

        fetchImageUrls();
    }, []);

    const checkForNewImage = async (imageName) => {
        const token = await getAccessTokenSilently();
        const response = await fetch(`/api/v1/media/storytellers/names/${imageName}/events`,
            {
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json',
                    Authorization: `Bearer ${token}`,
                }
            });

            const data = await response.json()
            const newImageUrl = data.location
            console.log(data)
            console.log(newImageUrl)
            setImageUrls([...imageUrls, newImageUrl]);
    };

    if (loadingImages) {
        return <Loading/>;
    }

    if (errorImages) {
        return <Error message={errorImages.message}/>;
    }

    return (
        <div>
            <h1>Images</h1>
            <h2>Uploaded Images</h2>
            <Carousel>
                {imageUrls.map((imageUrl, index) => (
                    <img key={`Image_${index}`} src={imageUrl} alt={`Image ${index}`} />
                ))}
            </Carousel>
            <h2>Upload New Image</h2>
            <ImageUpload storytellerId={storytellerId} newImageCallback={checkForNewImage}/>
        </div>
    );
}

export default Uploads;