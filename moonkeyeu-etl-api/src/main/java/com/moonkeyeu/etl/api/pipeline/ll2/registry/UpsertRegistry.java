package com.moonkeyeu.etl.api.pipeline.ll2.registry;

import com.moonkeyeu.etl.api.pipeline.ll2.Table;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.AgencyRows;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.CrewRows;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.ImageRow;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.LandingRows;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.LaunchRows;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.LauncherRows;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.MissionRows;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.PadRows;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.ProgramRows;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.RocketRows;
import com.moonkeyeu.etl.api.pipeline.ll2.rows.SpacecraftRows;
import com.moonkeyeu.etl.api.configuration.batch.writers.Upsert;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

/**
 * Every table's write statement, declared once.
 * <p>
 * Column names here are checked against {@code db/dev/001_moonkey_db.sql}. Reference tables that
 * upstream never revises use {@code keepExisting()} so a re-run skips the write entirely instead of
 * rewriting identical values; everything else merges, never overwriting a stored value with a null.
 * <p>
 * Image URLs are {@code insertOnly}: the media step owns them after the first insert.
 */
@Component
public class UpsertRegistry {

    private final Map<Table, Upsert<?>> upserts = new EnumMap<>(Table.class);

    @PostConstruct
    public void initRegistry() {
        registerReferenceData();
        registerCoreEntities();
        registerJoins();
        registerImages();
        registerLaunchAndChildren();
    }

    public <T> Upsert<T> get(Table table) {
        Upsert<T> upsert = (Upsert<T>) upserts.get(table);
        if (upsert == null) {
            throw new IllegalStateException("No upsert registered for table " + table);
        }
        return upsert;
    }

    private void put(Upsert<?> upsert) {
        upserts.put(upsert.table(), upsert);
    }

    private void registerReferenceData() {
        put(Upsert.<AgencyRows.AgencyType>into(Table.AGENCY_TYPE)
                .col("id", AgencyRows.AgencyType::id)
                .col("name", AgencyRows.AgencyType::name)
                .keepExisting());

        put(Upsert.<SpacecraftRows.SpacecraftType>into(Table.SPACECRAFT_TYPE)
                .col("id", SpacecraftRows.SpacecraftType::id)
                .col("name", SpacecraftRows.SpacecraftType::name)
                .keepExisting());

        put(Upsert.<SpacecraftRows.SpacecraftStatus>into(Table.SPACECRAFT_STATUS)
                .col("id", SpacecraftRows.SpacecraftStatus::id)
                .col("name", SpacecraftRows.SpacecraftStatus::name)
                .keepExisting());

        put(Upsert.<MissionRows.Orbit>into(Table.ORBIT)
                .col("id", MissionRows.Orbit::id)
                .col("name", MissionRows.Orbit::name)
                .col("abbrev", MissionRows.Orbit::abbrev)
                .keepExisting());

        put(Upsert.<LaunchRows.LaunchStatus>into(Table.LAUNCH_STATUS)
                .col("id", LaunchRows.LaunchStatus::id)
                .col("name", LaunchRows.LaunchStatus::name)
                .col("abbrev", LaunchRows.LaunchStatus::abbrev)
                .col("description", LaunchRows.LaunchStatus::description)
                .merge());

        put(Upsert.<LaunchRows.NetPrecision>into(Table.NET_PRECISION)
                .col("id", LaunchRows.NetPrecision::id)
                .col("name", LaunchRows.NetPrecision::name)
                .col("abbrev", LaunchRows.NetPrecision::abbrev)
                .col("description", LaunchRows.NetPrecision::description)
                .keepExisting());

        put(Upsert.<LandingRows.LandingType>into(Table.LANDING_TYPE)
                .col("id", LandingRows.LandingType::id)
                .col("name", LandingRows.LandingType::name)
                .col("abbrev", LandingRows.LandingType::abbrev)
                .col("description", LandingRows.LandingType::description)
                .keepExisting());

        put(Upsert.<CrewRows.AstronautStatus>into(Table.ASTRONAUT_STATUS)
                .col("id", CrewRows.AstronautStatus::id)
                .col("name", CrewRows.AstronautStatus::name)
                .keepExisting());

        put(Upsert.<CrewRows.Role>into(Table.ROLE)
                .col("id", CrewRows.Role::id)
                .col("name", CrewRows.Role::name)
                .keepExisting());

        put(Upsert.<LauncherRows.LauncherStatus>into(Table.LAUNCHER_STATUS)
                .col("id", LauncherRows.LauncherStatus::id)
                .col("name", LauncherRows.LauncherStatus::name)
                .keepExisting());

        put(Upsert.<AgencyRows.Country>into(Table.COUNTRY)
                .col("id", AgencyRows.Country::id)
                .col("name", AgencyRows.Country::name)
                .col("alpha_2_code", AgencyRows.Country::alpha2Code)
                .col("alpha_3_code", AgencyRows.Country::alpha3Code)
                .col("nationality_name", AgencyRows.Country::nationalityName)
                .col("nationality_name_composed", AgencyRows.Country::nationalityNameComposed)
                .keepExisting());

        put(Upsert.<ProgramRows.ProgramType>into(Table.PROGRAM_TYPE)
                .col("id", ProgramRows.ProgramType::id)
                .col("name", ProgramRows.ProgramType::name)
                .keepExisting());
    }

    private void registerCoreEntities() {
        put(Upsert.<AgencyRows.Agency>into(Table.AGENCIES)
                .col("id", AgencyRows.Agency::id)
                .col("name", AgencyRows.Agency::name)
                .col("featured", AgencyRows.Agency::featured)
                .col("type_id", AgencyRows.Agency::typeId)
                .col("abbrev", AgencyRows.Agency::abbrev)
                .col("description", AgencyRows.Agency::description)
                .col("administrator", AgencyRows.Agency::administrator)
                .col("founding_year", AgencyRows.Agency::foundingYear)
                .col("launchers", AgencyRows.Agency::launchers)
                .col("spacecraft", AgencyRows.Agency::spacecraft)
                .col("total_launch_count", AgencyRows.Agency::totalLaunchCount)
                .col("consecutive_successful_launches", AgencyRows.Agency::consecutiveSuccessfulLaunches)
                .col("successful_launches", AgencyRows.Agency::successfulLaunches)
                .col("failed_launches", AgencyRows.Agency::failedLaunches)
                .col("pending_launches", AgencyRows.Agency::pendingLaunches)
                .col("consecutive_successful_landings", AgencyRows.Agency::consecutiveSuccessfulLandings)
                .col("successful_landings", AgencyRows.Agency::successfulLandings)
                .col("failed_landings", AgencyRows.Agency::failedLandings)
                .col("attempted_landings", AgencyRows.Agency::attemptedLandings)
                .col("info_url", AgencyRows.Agency::infoUrl)
                .col("wiki_url", AgencyRows.Agency::wikiUrl)
                .merge());

        put(Upsert.<MissionRows.Mission>into(Table.MISSION)
                .col("id", MissionRows.Mission::id)
                .col("name", MissionRows.Mission::name)
                .col("description", MissionRows.Mission::description)
                .col("type", MissionRows.Mission::type)
                .col("orbit_id", MissionRows.Mission::orbitId)
                .merge());

        put(Upsert.<SpacecraftRows.SpacecraftConfiguration>into(Table.SPACECRAFT_CONFIGURATION)
                .col("id", SpacecraftRows.SpacecraftConfiguration::id)
                .col("name", SpacecraftRows.SpacecraftConfiguration::name)
                .col("type_id", SpacecraftRows.SpacecraftConfiguration::typeId)
                .col("in_use", SpacecraftRows.SpacecraftConfiguration::inUse)
                .col("capability", SpacecraftRows.SpacecraftConfiguration::capability)
                .col("history", SpacecraftRows.SpacecraftConfiguration::history)
                .col("details", SpacecraftRows.SpacecraftConfiguration::details)
                .col("maiden_flight", SpacecraftRows.SpacecraftConfiguration::maidenFlight)
                .col("height", SpacecraftRows.SpacecraftConfiguration::height)
                .col("diameter", SpacecraftRows.SpacecraftConfiguration::diameter)
                .col("human_rated", SpacecraftRows.SpacecraftConfiguration::humanRated)
                .col("crew_capacity", SpacecraftRows.SpacecraftConfiguration::crewCapacity)
                .col("payload_capacity", SpacecraftRows.SpacecraftConfiguration::payloadCapacity)
                .col("payload_return_capacity", SpacecraftRows.SpacecraftConfiguration::payloadReturnCapacity)
                .col("flight_life", SpacecraftRows.SpacecraftConfiguration::flightLife)
                .col("wiki_link", SpacecraftRows.SpacecraftConfiguration::wikiLink)
                .col("info_link", SpacecraftRows.SpacecraftConfiguration::infoLink)
                .col("agency_id", SpacecraftRows.SpacecraftConfiguration::agencyId)
                .merge());

        put(Upsert.<RocketRows.RocketConfiguration>into(Table.ROCKET_CONFIGURATION)
                .col("id", RocketRows.RocketConfiguration::id)
                .col("name", RocketRows.RocketConfiguration::name)
                .col("variant", RocketRows.RocketConfiguration::variant)
                .col("fullname", RocketRows.RocketConfiguration::fullname)
                .col("active", RocketRows.RocketConfiguration::active)
                .col("reusable", RocketRows.RocketConfiguration::reusable)
                .col("description", RocketRows.RocketConfiguration::description)
                .col("alias", RocketRows.RocketConfiguration::alias)
                .col("min_stage", RocketRows.RocketConfiguration::minStage)
                .col("max_stage", RocketRows.RocketConfiguration::maxStage)
                .col("maiden_flight", RocketRows.RocketConfiguration::maidenFlight)
                .col("length", RocketRows.RocketConfiguration::length)
                .col("diameter", RocketRows.RocketConfiguration::diameter)
                .col("launch_cost", RocketRows.RocketConfiguration::launchCost)
                .col("launch_mass", RocketRows.RocketConfiguration::launchMass)
                .col("leo_capacity", RocketRows.RocketConfiguration::leoCapacity)
                .col("gto_capacity", RocketRows.RocketConfiguration::gtoCapacity)
                .col("geo_capacity", RocketRows.RocketConfiguration::geoCapacity)
                .col("sso_capacity", RocketRows.RocketConfiguration::ssoCapacity)
                .col("to_thrust", RocketRows.RocketConfiguration::toThrust)
                .col("apogee", RocketRows.RocketConfiguration::apogee)
                .col("info_url", RocketRows.RocketConfiguration::infoUrl)
                .col("wiki_url", RocketRows.RocketConfiguration::wikiUrl)
                .col("total_launch_count", RocketRows.RocketConfiguration::totalLaunchCount)
                .col("consecutive_successful_launches", RocketRows.RocketConfiguration::consecutiveSuccessfulLaunches)
                .col("successful_launches", RocketRows.RocketConfiguration::successfulLaunches)
                .col("failed_launches", RocketRows.RocketConfiguration::failedLaunches)
                .col("pending_launches", RocketRows.RocketConfiguration::pendingLaunches)
                .col("attempted_landings", RocketRows.RocketConfiguration::attemptedLandings)
                .col("successful_landings", RocketRows.RocketConfiguration::successfulLandings)
                .col("failed_landings", RocketRows.RocketConfiguration::failedLandings)
                .col("consecutive_successful_landings", RocketRows.RocketConfiguration::consecutiveSuccessfulLandings)
                .col("agency_id", RocketRows.RocketConfiguration::agencyId)
                .col("image_id", RocketRows.RocketConfiguration::imageId)
                .merge());

        put(Upsert.<PadRows.Location>into(Table.LOCATION)
                .col("id", PadRows.Location::id)
                .col("name", PadRows.Location::name)
                .col("description", PadRows.Location::description)
                .col("map_image", PadRows.Location::mapImage)
                .col("location_timezone", PadRows.Location::locationTimezone)
                .col("total_launch_count", PadRows.Location::totalLaunchCount)
                .col("total_landing_count", PadRows.Location::totalLandingCount)
                .merge());

        put(Upsert.<PadRows.LaunchPad>into(Table.LAUNCH_PAD)
                .col("id", PadRows.LaunchPad::id)
                .col("name", PadRows.LaunchPad::name)
                .col("active", PadRows.LaunchPad::active)
                .col("description", PadRows.LaunchPad::description)
                .col("info_url", PadRows.LaunchPad::infoUrl)
                .col("wiki_url", PadRows.LaunchPad::wikiUrl)
                .col("map_url", PadRows.LaunchPad::mapUrl)
                .col("latitude", PadRows.LaunchPad::latitude)
                .col("longitude", PadRows.LaunchPad::longitude)
                .insertOnly("map_image", PadRows.LaunchPad::mapImage)
                .col("total_launch_count", PadRows.LaunchPad::totalLaunchCount)
                .col("orbital_launch_attempt_count", PadRows.LaunchPad::orbitalLaunchAttemptCount)
                .col("location_id", PadRows.LaunchPad::locationId)
                .merge());

        put(Upsert.<LandingRows.LandingZone>into(Table.LANDING_ZONE)
                .col("id", LandingRows.LandingZone::id)
                .col("name", LandingRows.LandingZone::name)
                .col("abbrev", LandingRows.LandingZone::abbrev)
                .col("description", LandingRows.LandingZone::description)
                .col("successful_landings", LandingRows.LandingZone::successfulLandings)
                .col("location_id", LandingRows.LandingZone::locationId)
                .merge());

        put(Upsert.<LauncherRows.Launcher>into(Table.LAUNCHER)
                .col("id", LauncherRows.Launcher::id)
                .col("details", LauncherRows.Launcher::details)
                .col("flight_proven", LauncherRows.Launcher::flightProven)
                .col("serial_number", LauncherRows.Launcher::serialNumber)
                .col("successful_landings", LauncherRows.Launcher::successfulLandings)
                .col("attempted_landings", LauncherRows.Launcher::attemptedLandings)
                .col("flights", LauncherRows.Launcher::flights)
                .col("last_launch_date", LauncherRows.Launcher::lastLaunchDate)
                .col("first_launch_date", LauncherRows.Launcher::firstLaunchDate)
                .col("status_id", LauncherRows.Launcher::statusId)
                .merge());

        put(Upsert.<CrewRows.Astronaut>into(Table.ASTRONAUT)
                .col("id", CrewRows.Astronaut::id)
                .col("name", CrewRows.Astronaut::name)
                .col("in_space", CrewRows.Astronaut::inSpace)
                .col("date_of_death", CrewRows.Astronaut::dateOfDeath)
                .col("date_of_birth", CrewRows.Astronaut::dateOfBirth)
                .col("age", CrewRows.Astronaut::age)
                .col("bio", CrewRows.Astronaut::bio)
                .col("wiki_url", CrewRows.Astronaut::wikiUrl)
                .col("last_flight", CrewRows.Astronaut::lastFlight)
                .col("first_flight", CrewRows.Astronaut::firstFlight)
                .col("status_id", CrewRows.Astronaut::statusId)
                .col("agency_id", CrewRows.Astronaut::agencyId)
                .merge());

        put(Upsert.<ProgramRows.Program>into(Table.PROGRAMS)
                .col("id", ProgramRows.Program::id)
                .col("name", ProgramRows.Program::name)
                .col("info_url", ProgramRows.Program::infoUrl)
                .col("wiki_url", ProgramRows.Program::wikiUrl)
                .col("description", ProgramRows.Program::description)
                .col("start_date", ProgramRows.Program::startDate)
                .col("type_id", ProgramRows.Program::typeId)
                .merge());

        put(Upsert.<RocketRows.Rocket>into(Table.ROCKET)
                .col("id", RocketRows.Rocket::id)
                .col("rocket_conf_id", RocketRows.Rocket::rocketConfId)
                .merge());

        put(Upsert.<LandingRows.Landing>into(Table.LANDING)
                .col("id", LandingRows.Landing::id)
                .col("attempt", LandingRows.Landing::attempt)
                .col("success", LandingRows.Landing::success)
                .col("description", LandingRows.Landing::description)
                .col("downrange_distance", LandingRows.Landing::downrangeDistance)
                .col("landing_zone_id", LandingRows.Landing::landingZoneId)
                .col("landing_type_id", LandingRows.Landing::landingTypeId)
                .merge());

        put(Upsert.<SpacecraftRows.Spacecraft>into(Table.SPACECRAFT)
                .col("id", SpacecraftRows.Spacecraft::id)
                .col("name", SpacecraftRows.Spacecraft::name)
                .col("serial_number", SpacecraftRows.Spacecraft::serialNumber)
                .col("is_placeholder", SpacecraftRows.Spacecraft::isPlaceholder)
                .col("in_space", SpacecraftRows.Spacecraft::inSpace)
                .col("flights_count", SpacecraftRows.Spacecraft::flightsCount)
                .col("mission_ends_count", SpacecraftRows.Spacecraft::missionEndsCount)
                .col("description", SpacecraftRows.Spacecraft::description)
                .col("spacecraft_conf_id", SpacecraftRows.Spacecraft::spacecraftConfId)
                .col("status_id", SpacecraftRows.Spacecraft::statusId)
                .merge());
    }

    private void registerJoins() {
        put(Upsert.<MissionRows.MissionHasAgencies>into(Table.MISSION_HAS_AGENCIES)
                .col("id", MissionRows.MissionHasAgencies::id)
                .col("mission_id", MissionRows.MissionHasAgencies::missionId)
                .col("agency_id", MissionRows.MissionHasAgencies::agencyId)
                .keepExisting());

        put(Upsert.<PadRows.PadHasAgencies>into(Table.PAD_HAS_AGENCIES)
                .col("id", PadRows.PadHasAgencies::id)
                .col("launch_pad_id", PadRows.PadHasAgencies::launchPadId)
                .col("agency_id", PadRows.PadHasAgencies::agencyId)
                .keepExisting());

        put(Upsert.<AgencyRows.AgencyHasCountry>into(Table.AGENCIES_HAS_COUNTRY)
                .col("id", AgencyRows.AgencyHasCountry::id)
                .col("agency_id", AgencyRows.AgencyHasCountry::agencyId)
                .col("country_id", AgencyRows.AgencyHasCountry::countryId)
                .keepExisting());

        put(Upsert.<CrewRows.AstronautHasCountry>into(Table.ASTRONAUT_HAS_COUNTRY)
                .col("id", CrewRows.AstronautHasCountry::id)
                .col("astronaut_id", CrewRows.AstronautHasCountry::astronautId)
                .col("country_id", CrewRows.AstronautHasCountry::countryId)
                .keepExisting());

        put(Upsert.<ProgramRows.ProgramHasAgencies>into(Table.PROGRAMS_HAS_AGENCIES)
                .col("id", ProgramRows.ProgramHasAgencies::id)
                .col("program_id", ProgramRows.ProgramHasAgencies::programId)
                .col("agency_id", ProgramRows.ProgramHasAgencies::agencyId)
                .keepExisting());

        put(Upsert.<ProgramRows.LaunchHasProgram>into(Table.LAUNCH_HAS_PROGRAMS)
                .col("id", ProgramRows.LaunchHasProgram::id)
                .col("program_id", ProgramRows.LaunchHasProgram::programId)
                .col("launch_id", ProgramRows.LaunchHasProgram::launchId)
                .keepExisting());
    }


    private void registerImages() {
        put(imageUpsert(Table.ROCKET_CONF_IMAGES, null));
        put(imageUpsert(Table.AGENCIES_IMAGES, "agency_id"));
        put(imageUpsert(Table.SPACECRAFT_CONF_IMAGES, "spacecraft_conf_id"));
        put(imageUpsert(Table.LAUNCHER_IMAGES, "launcher_id"));
        put(imageUpsert(Table.ASTRONAUT_IMAGES, "astronaut_id"));
        put(imageUpsert(Table.LAUNCH_PAD_IMAGES, "launch_pad_id"));
        put(imageUpsert(Table.PROGRAMS_IMAGES, "program_id"));

        put(Upsert.<CrewRows.SocialMedia>into(Table.SOCIAL_MEDIA)
                .col("id", CrewRows.SocialMedia::id)
                .col("name", CrewRows.SocialMedia::name)
                .col("media_url", CrewRows.SocialMedia::mediaUrl)
                .col("astronaut_id", CrewRows.SocialMedia::astronautId)
                .merge());
    }

    /**
     * The seven image tables differ only in the name of their owning column — and
     * {@code rocket_conf_images} has none, because the configuration points at the image instead.
     */
    private static Upsert<ImageRow> imageUpsert(Table table, String ownerColumn) {
        Upsert.Builder<ImageRow> builder = Upsert.<ImageRow>into(table)
                .col("id", ImageRow::id)
                .col("name", ImageRow::name)
                .insertOnly("image_url", ImageRow::imageUrl)
                .insertOnly("thumbnail_url", ImageRow::thumbnailUrl)
                .col("credit", ImageRow::credit);

        if (ownerColumn != null) {
            builder.col(ownerColumn, ImageRow::ownerId);
        }
        return builder.merge();
    }

    private void registerLaunchAndChildren() {
        put(Upsert.<LaunchRows.Launch>into(Table.LAUNCH)
                .col("id", LaunchRows.Launch::id)
                .col("slug", LaunchRows.Launch::slug)
                .col("flightclub_url", LaunchRows.Launch::flightclubUrl)
                .col("name", LaunchRows.Launch::name)
                .col("last_updated", LaunchRows.Launch::lastUpdated)
                .col("net", LaunchRows.Launch::net)
                .col("window_end", LaunchRows.Launch::windowEnd)
                .col("window_start", LaunchRows.Launch::windowStart)
                .col("probability", LaunchRows.Launch::probability)
                .col("weather_concerns", LaunchRows.Launch::weatherConcerns)
                .col("agency_id", LaunchRows.Launch::agencyId)
                .col("rocket_id", LaunchRows.Launch::rocketId)
                .col("mission_id", LaunchRows.Launch::missionId)
                .col("launch_pad_id", LaunchRows.Launch::launchPadId)
                .col("status_id", LaunchRows.Launch::statusId)
                .col("net_precision_id", LaunchRows.Launch::netPrecisionId)
                .merge());

        put(Upsert.<SpacecraftRows.SpacecraftStage>into(Table.SPACECRAFT_STAGE)
                .col("id", SpacecraftRows.SpacecraftStage::id)
                .col("mission_end", SpacecraftRows.SpacecraftStage::missionEnd)
                .col("destination", SpacecraftRows.SpacecraftStage::destination)
                .col("spacecraft_id", SpacecraftRows.SpacecraftStage::spacecraftId)
                .col("rocket_id", SpacecraftRows.SpacecraftStage::rocketId)
                .col("landing_id", SpacecraftRows.SpacecraftStage::landingId)
                .merge());

        put(Upsert.<LauncherRows.LauncherStage>into(Table.LAUNCHER_STAGE)
                .col("id", LauncherRows.LauncherStage::id)
                .col("type", LauncherRows.LauncherStage::type)
                .col("reused", LauncherRows.LauncherStage::reused)
                .col("launcher_flight_number", LauncherRows.LauncherStage::launcherFlightNumber)
                .col("rocket_id", LauncherRows.LauncherStage::rocketId)
                .col("launcher_id", LauncherRows.LauncherStage::launcherId)
                .col("landing_id", LauncherRows.LauncherStage::landingId)
                .merge());

        put(Upsert.<LaunchRows.Video>into(Table.VIDEOS)
                .col("id", LaunchRows.Video::id)
                .col("priority", LaunchRows.Video::priority)
                .col("source", LaunchRows.Video::source)
                .col("publisher", LaunchRows.Video::publisher)
                .col("title", LaunchRows.Video::title)
                .col("description", LaunchRows.Video::description)
                .col("feature_image", LaunchRows.Video::featureImage)
                .col("video_url", LaunchRows.Video::videoUrl)
                .col("launch_id", LaunchRows.Video::launchId)
                .merge());

        put(Upsert.<LaunchRows.Update>into(Table.UPDATES)
                .col("id", LaunchRows.Update::id)
                .col("profile_image", LaunchRows.Update::profileImage)
                .col("comment", LaunchRows.Update::comment)
                .col("info_url", LaunchRows.Update::infoUrl)
                .col("created_by", LaunchRows.Update::createdBy)
                .col("created_on", LaunchRows.Update::createdOn)
                .col("launch_id", LaunchRows.Update::launchId)
                .merge());

        put(Upsert.<LaunchRows.InfoUrl>into(Table.INFO_URLS)
                .col("id", LaunchRows.InfoUrl::id)
                .col("priority", LaunchRows.InfoUrl::priority)
                .col("source", LaunchRows.InfoUrl::source)
                .col("title", LaunchRows.InfoUrl::title)
                .col("description", LaunchRows.InfoUrl::description)
                .col("feature_image", LaunchRows.InfoUrl::featureImage)
                .col("url", LaunchRows.InfoUrl::url)
                .col("launch_id", LaunchRows.InfoUrl::launchId)
                .merge());

        put(Upsert.<LaunchRows.MissionPatch>into(Table.MISSION_PATCHES)
                .col("id", LaunchRows.MissionPatch::id)
                .col("priority", LaunchRows.MissionPatch::priority)
                .col("name", LaunchRows.MissionPatch::name)
                .insertOnly("image_url", LaunchRows.MissionPatch::imageUrl)
                .col("launch_id", LaunchRows.MissionPatch::launchId)
                .merge());

        put(Upsert.<CrewRows.CrewMember>into(Table.CREW_MEMBER)
                .col("id", CrewRows.CrewMember::id)
                .col("astronaut_id", CrewRows.CrewMember::astronautId)
                .col("role_id", CrewRows.CrewMember::roleId)
                .col("spacecraft_stage_id", CrewRows.CrewMember::spacecraftStageId)
                .col("launch_id", CrewRows.CrewMember::launchId)
                .merge());
    }
}
