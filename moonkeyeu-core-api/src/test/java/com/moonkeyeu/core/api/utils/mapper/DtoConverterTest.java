package com.moonkeyeu.core.api.utils.mapper;

import com.moonkeyeu.core.api.launch.dto.CountryDTO;
import com.moonkeyeu.core.api.launch.model.country.Country;
import com.moonkeyeu.core.api.utils.mapper.DtoConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;

class DtoConverterTest {
    private DtoConverter dtoConverter;

    @BeforeEach
    void setUp() {
        dtoConverter = new DtoConverter(new ModelMapper());
    }

    @Test
    public void whenConvertCountryEntityToCountryDto_thenCorrect() {
        Country country = new Country();
        country.setCountryId(1L);
        country.setCountryName("America");
        country.setNationalityName("American");

        CountryDTO countryDTO = this.dtoConverter.convertToDto(country, CountryDTO.class);
        assertEquals(country.getCountryId(), countryDTO.getCountryId());
        assertEquals(country.getCountryName(), countryDTO.getCountryName());
        assertEquals(country.getNationalityName(), countryDTO.getNationalityName());
    }

    @Test
    public void whenConvertCountryDtoToCountryEntity_thenCorrect() {
       CountryDTO countryDTO = new CountryDTO();
       countryDTO.setCountryId(1L);
       countryDTO.setCountryName("America");
       countryDTO.setNationalityName("American");

        Country country = this.dtoConverter.convertToEntity(countryDTO, Country.class);
        assertEquals(country.getCountryId(), countryDTO.getCountryId());
        assertEquals(country.getCountryName(), countryDTO.getCountryName());
        assertEquals(country.getNationalityName(), countryDTO.getNationalityName());
    }
}