package com.moonkeyeu.core.api.launch.services;

import com.moonkeyeu.core.api.launch.dto.NasaApodDTO;
import com.moonkeyeu.core.api.settings.exceptions.NasaApodFetchException;

public interface NasaApodClientService {
    NasaApodDTO fetchNasaAstronomyPictureOfTheDay() throws NasaApodFetchException;
}
