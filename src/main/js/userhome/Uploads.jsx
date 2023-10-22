import React, {useEffect, useState} from 'react';
import { Carousel } from 'react-responsive-carousel';
import 'react-responsive-carousel/lib/styles/carousel.min.css';
import {Loading} from "../Loading";
import {Error} from "../Error";
import {useAuth0} from "@auth0/auth0-react";

function Uploads({ storytellerId }) {
    const { getAccessTokenSilently } = useAuth0();
    const [imageUrls, setImageUrls] = useState([]);
    const [loadingImages, setLoadingImages] = useState(true)
    const [errorImages, setErrorImages] = useState(null)

    useEffect(() => {
        async function fetchImageUrls() {
            try {
                console.log('fetching');
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
            <img key="testy" src="http://localhost:8080/api/v1/uploads/images/proxy/8ub5lac5.648b65e98860a0c976f61bf7_IMG_3951.HEIC" alt="Image 3"/>
            <Carousel>
                {imageUrls.map((imageUrl, index) => (
                    <img key={`Image_${index}`} src={imageUrl} alt={`Image ${index}`} />
                ))}
            </Carousel>
        </div>
    );
}

export default Uploads;