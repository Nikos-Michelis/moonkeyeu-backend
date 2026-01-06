package com.moonkeyeu.etl.api.configuration.files;
import com.moonkeyeu.etl.api.model.CsvEntity;
import com.moonkeyeu.etl.api.model.LocationEntity;
import com.moonkeyeu.etl.api.model.agency.AgenciesEntity;
import com.moonkeyeu.etl.api.model.agency.AgencyTypeEntity;
import com.moonkeyeu.etl.api.model.country.AgenciesHasCountryEntity;
import com.moonkeyeu.etl.api.model.country.AstronautHasCountryEntity;
import com.moonkeyeu.etl.api.model.country.CountryEntity;
import com.moonkeyeu.etl.api.model.crew.AstronautEntity;
import com.moonkeyeu.etl.api.model.crew.AstronautStatusEntity;
import com.moonkeyeu.etl.api.model.crew.CrewMemberEntity;
import com.moonkeyeu.etl.api.model.crew.RoleEntity;
import com.moonkeyeu.etl.api.model.images.*;
import com.moonkeyeu.etl.api.model.landing.LandingEntity;
import com.moonkeyeu.etl.api.model.landing.LandingTypeEntity;
import com.moonkeyeu.etl.api.model.landing.LandingZoneEntity;
import com.moonkeyeu.etl.api.model.images.SpacecraftImagesEntity;
import com.moonkeyeu.etl.api.model.launch.LaunchEntity;
import com.moonkeyeu.etl.api.model.launch.LaunchStatusEntity;
import com.moonkeyeu.etl.api.model.launcher.LauncherEntity;
import com.moonkeyeu.etl.api.model.launcher.LauncherStageEntity;
import com.moonkeyeu.etl.api.model.launcher.LauncherStatusEntity;
import com.moonkeyeu.etl.api.model.media.*;
import com.moonkeyeu.etl.api.model.mission.MissionEntity;
import com.moonkeyeu.etl.api.model.mission.MissionHasAgenciesEntity;
import com.moonkeyeu.etl.api.model.mission.OrbitEntity;
import com.moonkeyeu.etl.api.model.pad.LaunchPadEntity;
import com.moonkeyeu.etl.api.model.pad.LaunchPadHasAgenciesEntity;
import com.moonkeyeu.etl.api.model.programs.LaunchHasProgramsEntity;
import com.moonkeyeu.etl.api.model.programs.ProgramTypeEntity;
import com.moonkeyeu.etl.api.model.programs.ProgramsEntity;
import com.moonkeyeu.etl.api.model.programs.ProgramsHasAgenciesEntity;
import com.moonkeyeu.etl.api.model.rocket.RocketConfigurationEntity;
import com.moonkeyeu.etl.api.model.rocket.RocketEntity;
import com.moonkeyeu.etl.api.model.spacecraft.*;
import lombok.Getter;

@Getter
public enum CsvGroup {

    COUNTRIES_AGENCIES(1, CountryEntity.class, CsvSource.RAW_AGENCY_COUNTRIES_CSV),
    
    AGENCY_TYPES(2, AgencyTypeEntity.class, CsvSource.RAW_AGENCIES_CSV),
    AGENCIES(3, AgenciesEntity.class, CsvSource.RAW_AGENCIES_CSV),
    AGENCY_IMAGES(4, AgenciesImagesEntity.class, CsvSource.RAW_AGENCIES_IMAGES_CSV),
    
    ROCKET_IMAGES(5, RocketImageEntity.class, CsvSource.RAW_LAUNCHES_CSV),
    ROCKET_CONFIG(6, RocketConfigurationEntity.class, CsvSource.RAW_LAUNCHES_CSV),
    ROCKETS(7, RocketEntity.class, CsvSource.RAW_LAUNCHES_CSV),
    LOCATIONS_LAUNCHES(8, LocationEntity.class, CsvSource.RAW_LAUNCHES_CSV),
    LAUNCH_PADS(9, LaunchPadEntity.class, CsvSource.RAW_LAUNCHES_CSV),
    LAUNCH_PAD_AGENCIES(10, LaunchPadHasAgenciesEntity.class, CsvSource.RAW_LAUNCHES_CSV),
    ORBITS(11, OrbitEntity.class, CsvSource.RAW_LAUNCHES_CSV),
    MISSIONS(12, MissionEntity.class, CsvSource.RAW_LAUNCHES_CSV),
    LAUNCH_STATUS(13, LaunchStatusEntity.class, CsvSource.RAW_LAUNCHES_CSV),
    LAUNCHES(14, LaunchEntity.class, CsvSource.RAW_LAUNCHES_CSV),
    LAUNCH_PAD_IMAGES(45, PadImagesEntity.class, CsvSource.RAW_LAUNCHES_CSV),

    LANDING_TYPES_BOOSTERS(15, LandingTypeEntity.class, CsvSource.RAW_BOOSTERS_CSV),
    LOCATIONS_BOOSTERS(16, LocationEntity.class, CsvSource.RAW_BOOSTERS_CSV),
    LANDING_ZONES_BOOSTERS(17, LandingZoneEntity.class, CsvSource.RAW_BOOSTERS_CSV),
    LANDINGS_BOOSTERS(18, LandingEntity.class, CsvSource.RAW_BOOSTERS_CSV),
    LAUNCHER_STATUS(19, LauncherStatusEntity.class, CsvSource.RAW_BOOSTERS_CSV),
    LAUNCHERS(20, LauncherEntity.class, CsvSource.RAW_BOOSTERS_CSV),
    LAUNCHER_STAGES(21, LauncherStageEntity.class, CsvSource.RAW_BOOSTERS_CSV),
    LAUNCHER_IMAGES(44, LauncherImagesEntity.class, CsvSource.RAW_BOOSTERS_CSV),

    ASTRONAUT_STATUS(22, AstronautStatusEntity.class, CsvSource.RAW_ASTRONAUTS_CSV),
    ROLES(23, RoleEntity.class, CsvSource.RAW_ASTRONAUTS_CSV),
    ASTRONAUTS(24, AstronautEntity.class, CsvSource.RAW_ASTRONAUTS_CSV),
    CREW_MEMBERS(34, CrewMemberEntity.class, CsvSource.RAW_ASTRONAUTS_CSV),
    ASTRONAUT_IMAGES(43, AstronautImagesEntity.class, CsvSource.RAW_ASTRONAUTS_CSV),

    LANDING_TYPES_SPACECRAFT(25, LandingTypeEntity.class, CsvSource.RAW_SPACECRAFT_STAGE_CSV),
    LOCATIONS_SPACECRAFT(26, LocationEntity.class, CsvSource.RAW_SPACECRAFT_STAGE_CSV),
    LANDING_ZONES_SPACECRAFT(27, LandingZoneEntity.class, CsvSource.RAW_SPACECRAFT_STAGE_CSV),
    LANDINGS_SPACECRAFT(28, LandingEntity.class, CsvSource.RAW_SPACECRAFT_STAGE_CSV),
    SPACECRAFT_TYPES(29, SpacecraftTypeEntity.class, CsvSource.RAW_SPACECRAFT_STAGE_CSV),
    SPACECRAFT_STATUS(30, SpacecraftStatusEntity.class, CsvSource.RAW_SPACECRAFT_STAGE_CSV),
    SPACECRAFT_CONFIG(31, SpacecraftConfigurationEntity.class, CsvSource.RAW_SPACECRAFT_STAGE_CSV),
    SPACECRAFT(32, SpacecraftEntity.class, CsvSource.RAW_SPACECRAFT_STAGE_CSV),
    SPACECRAFT_STAGES(33, SpacecraftStageEntity.class, CsvSource.RAW_SPACECRAFT_STAGE_CSV),
    SPACECRAFT_IMAGES(46, SpacecraftImagesEntity.class, CsvSource.RAW_SPACECRAFT_STAGE_CSV),

    MISSION_PATCHES(35, MissionPatchesEntity.class, CsvSource.RAW_MISSION_PATCHES_CSV),
    VIDEOS(36, VideoEntity.class, CsvSource.RAW_VIDEO_CSV),
    UPDATES(37, UpdatesEntity.class, CsvSource.RAW_UPDATES_CSV),
    INFO_URLS(38, InfoUrlsEntity.class, CsvSource.RAW_INFO_URLS_CSV),
    SOCIAL_MEDIA(39, SocialMediaEntity.class, CsvSource.RAW_SOCIAL_MEDIA_CSV),
    COUNTRIES_ASTRONAUTS(40, CountryEntity.class, CsvSource.RAW_ASTRONAUTS_COUNTRIES_CSV),
    
    PROGRAM_TYPES(41, ProgramTypeEntity.class, CsvSource.RAW_PROGRAMS_CSV),
    PROGRAMS(42, ProgramsEntity.class, CsvSource.RAW_PROGRAMS_CSV),
    PROGRAM_IMAGES(45, ProgramsImagesEntity.class, CsvSource.RAW_PROGRAMS_CSV),
    LAUNCH_PROGRAMS(53, LaunchHasProgramsEntity.class, CsvSource.RAW_PROGRAMS_CSV),

    MISSION_AGENCIES(48, MissionHasAgenciesEntity.class, CsvSource.RAW_MISSIONS_AGENCIES_CSV),
    AGENCIES_COUNTRIES_PROVIDERS(49, AgenciesHasCountryEntity.class, CsvSource.LAUNCH_PROVIDERS_COUNTRIES_CSV),
    AGENCIES_COUNTRIES_MANUFACTURERS(50, AgenciesHasCountryEntity.class, CsvSource.MANUFACTURER_COUNTRIES_CSV),
    ASTRONAUT_COUNTRIES(51, AstronautHasCountryEntity.class, CsvSource.ASTRONAUTS_COUNTRIES_CSV),
    PROGRAM_AGENCIES(52, ProgramsHasAgenciesEntity.class, CsvSource.PROGRAMS_AGENCIES_CSV);

    private final int order;
    private final Class<? extends CsvEntity<?>> entityClass;
    private final CsvSource csvSource;

    CsvGroup(int order,
             Class<? extends CsvEntity<?>> entityClass,
             CsvSource csvSource) {
        this.order = order;
        this.entityClass = entityClass;
        this.csvSource = csvSource;
    }
}