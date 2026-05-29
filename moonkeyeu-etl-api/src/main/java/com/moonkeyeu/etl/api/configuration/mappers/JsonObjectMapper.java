package com.moonkeyeu.etl.api.configuration.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moonkeyeu.etl.api.dto.chunks.ChunkStore;
import com.moonkeyeu.etl.api.dto.Images.Image;
import com.moonkeyeu.etl.api.dto.launch.Launch;
import com.moonkeyeu.etl.api.dto.media.MissionPatches;
import com.moonkeyeu.etl.api.dto.agency.Agency;
import com.moonkeyeu.etl.api.dto.country.AgencyHasCountry;
import com.moonkeyeu.etl.api.dto.country.AstronautHasNationalities;
import com.moonkeyeu.etl.api.dto.country.Country;
import com.moonkeyeu.etl.api.dto.crew.LaunchCrew;
import com.moonkeyeu.etl.api.dto.media.InfoUrl;
import com.moonkeyeu.etl.api.dto.media.SocialMedia;
import com.moonkeyeu.etl.api.dto.media.Updates;
import com.moonkeyeu.etl.api.dto.media.Video;
import com.moonkeyeu.etl.api.dto.launcher.LauncherStage;
import com.moonkeyeu.etl.api.dto.program.Program;
import com.moonkeyeu.etl.api.dto.program.ProgramHasAgencies;
import com.moonkeyeu.etl.api.dto.spacecraft.SpaceCraftStage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

@Slf4j
public class JsonObjectMapper {
    public final ObjectMapper objectMapper;

    public JsonObjectMapper() {
        this.objectMapper = new ObjectMapper();
    }

    public ChunkStore JsonToAgenciesMapper(JsonNode rootNode, ChunkStore chunkStore) throws Exception {
        if (rootNode != null) {
            Agency agency = objectMapper.treeToValue(rootNode, Agency.class);
            chunkStore.getAgencies().add(agency);
            mapAgenciesImages(rootNode).ifPresent(images -> chunkStore.getAgenciesImages().add(images));
            chunkStore.getCountries().addAll(mapAgenciesCountries(rootNode));
            chunkStore.getAgencyHasCountries().addAll(mapAgencyHasCountries(rootNode));
        }
        return chunkStore;
    }

    public ChunkStore jsonToLaunchesMapper(JsonNode rootNode, ChunkStore chunkStore) throws Exception {
        if (rootNode != null) {
            chunkStore.getLaunches().add(objectMapper.treeToValue(rootNode, Launch.class));
            mapLaunchPadImages(rootNode).ifPresent(images -> chunkStore.getPadImages().add(images));
            mapRocketConfigImages(rootNode).ifPresent(images -> chunkStore.getRocketConfImages().add(images));
            mapSpacecraftConfigImages(rootNode).ifPresent(images -> chunkStore.getSpacecraftConfImages().add(images));
            chunkStore.getLauncherImages().addAll(mapLauncherImages(rootNode));
            chunkStore.getLauncherStage().addAll(mapLaunchersStage(rootNode));
            List<SpaceCraftStage> stages = mapSpaceCraftStage(rootNode);
            chunkStore.getSpacecraftStage().addAll(stages);
            mapCrewData(rootNode, stages, chunkStore);
            chunkStore.getVideoList().addAll(mapVideos(rootNode));
            chunkStore.getUpdates().addAll(mapUpdates(rootNode));
            mapProgramsData(rootNode, chunkStore);
            chunkStore.getPatches().addAll(mapMissionPatches(rootNode));
            chunkStore.getInfoUrls().addAll(mapInfoUrls(rootNode));
            chunkStore.getAgencies().addAll(mapMissionAgencies(rootNode));
        }
        return chunkStore;
    }

    public void mapCrewData(JsonNode rootNode, List<SpaceCraftStage> stages, ChunkStore chunkStore) throws Exception {
        String launchId = rootNode.get("id").asText();
        JsonNode stageNodes = rootNode.path("rocket").get("spacecraft_stage");
        for (int i = 0; i < stages.size(); i++) {
            JsonNode stageNode = stageNodes.get(i);
            int stageId = stageNode.get("id").asInt();
            JsonNode crewNodes = stageNode.get("launch_crew");
            if (crewNodes != null) {
                chunkStore.getCrewList().addAll(mapAstronauts(crewNodes, stageId, launchId));
                chunkStore.getNationalities().addAll(mapAstronautsHasNationalities(crewNodes));
                chunkStore.getCountries().addAll(mapCountries(crewNodes));
                mapAstronautImages(crewNodes).ifPresent(images -> chunkStore.getAstronautImages().add(images));
                chunkStore.getSocialMedia().addAll(mapSocialMediaLinks(crewNodes));
            }
        }
    }

    public void mapProgramsData(JsonNode rootNode, ChunkStore chunkStore) throws Exception {
        String launchId = rootNode.get("id").asText();
        JsonNode programNodes = rootNode.get("program");
        if (programNodes != null && programNodes.isArray()) {
            for (JsonNode programNode : programNodes) {
                chunkStore.getPrograms().add(mapPrograms(programNode, launchId));
                chunkStore.getProgramHasAgencies().addAll(mapProgramsHasAgencies(programNode));
            }
        }
    }

    public List<Agency> mapMissionAgencies(JsonNode resultsNode) {
        JsonNode missionAgencyNodes = resultsNode.path("mission").get("agencies");
        if (missionAgencyNodes == null || !missionAgencyNodes.isArray()) return List.of();
        Integer mission_id = resultsNode.path("mission").get("id").asInt();
        List<Agency> missionsHasAgencies = objectMapper.convertValue(missionAgencyNodes, new TypeReference<>() {});
        missionsHasAgencies.forEach(agency -> agency.setMission_id(mission_id));
        return missionsHasAgencies;
    }

    public List<LauncherStage> mapLaunchersStage(JsonNode resultsNode) {
        String id = resultsNode.get("id").asText();
        Integer rocket_id = resultsNode.path("rocket").get("id").asInt();
        JsonNode boostersNode = resultsNode.path("rocket").get("launcher_stage");
        if (boostersNode == null  || !boostersNode.isArray()) return List.of();
        List<LauncherStage> stages = objectMapper.convertValue(boostersNode, new TypeReference<>() {});
        stages.forEach(stage -> {
            stage.setLaunch_id(id);
            stage.setRocket_id(rocket_id);

        });
        return stages;
    }

    public List<SpaceCraftStage> mapSpaceCraftStage(JsonNode resultsNode) throws Exception {
        String id = resultsNode.get("id").asText();
        Integer rocket_id = resultsNode.path("rocket").get("id").asInt();
        JsonNode spacecraftStageNode = resultsNode.path("rocket").get("spacecraft_stage");
        if (spacecraftStageNode == null || !spacecraftStageNode.isArray()) return List.of();
        List<SpaceCraftStage> stages = objectMapper.convertValue(spacecraftStageNode, new TypeReference<>() {});
        stages.forEach(stage -> {
            stage.setLaunch_id(id);
            stage.setRocket_id(rocket_id);
        });
        return stages;
    }

    public List<LaunchCrew> mapAstronauts(JsonNode crewNodes, Integer spacecraftStageId, String launchId) throws IllegalArgumentException {
        if (crewNodes == null || !crewNodes.isArray()) return List.of();
        List<LaunchCrew> crewList = objectMapper.convertValue(crewNodes, new TypeReference<>() {});
        crewList.forEach(crew -> {
            crew.setSpacecraft_stage_id(spacecraftStageId);
            crew.setLaunch_id(launchId);
        });

        return crewList;
    }

    public List<AstronautHasNationalities> mapAstronautsHasNationalities(JsonNode crewNodes) {
        List<AstronautHasNationalities> nationalities = new ArrayList<>();
        for (JsonNode crewNode : crewNodes) {
            JsonNode nationalityNodes = crewNode.path("astronaut").get("nationality");
            if (nationalityNodes != null && !nationalityNodes.isEmpty() && nationalityNodes.isArray()) {
                Integer id = crewNode.path("astronaut").get("id").asInt();
                for (JsonNode nationalityNode : nationalityNodes) {
                    AstronautHasNationalities nationality = new AstronautHasNationalities();
                    nationality.setCountry_id(nationalityNode.get("id").asInt());
                    nationality.setAstronaut_id(id);
                    nationalities.add(nationality);
                }
            }
        }
        return nationalities;
    }

    public List<Country> mapCountries(JsonNode crewNodes) {
        return StreamSupport.stream(crewNodes.spliterator(), false)
                .map(node -> node.path("astronaut").get("nationality"))
                .filter(JsonNode::isArray)
                .flatMap(flatNode -> objectMapper.convertValue(flatNode, new TypeReference<List<Country>>() {}).stream())
                .toList();
    }

    public List<AgencyHasCountry> mapAgencyHasCountries(JsonNode resultsNode) {
        List<AgencyHasCountry> agencyHasCountries = new ArrayList<>();
        JsonNode countriesNode = resultsNode.get("country");
        Integer agencyId = resultsNode.get("id").asInt();
        if (countriesNode != null && !countriesNode.isEmpty() && countriesNode.isArray()) {
            for (JsonNode countryNode : countriesNode) {
                AgencyHasCountry agencyHasCountry = new AgencyHasCountry();
                agencyHasCountry.setAgency_id(agencyId);
                agencyHasCountry.setCountry_id(countryNode.get("id").asInt());
                agencyHasCountries.add(agencyHasCountry);
            }
        }
        return agencyHasCountries;
    }

    public List<Country> mapAgenciesCountries(JsonNode resultsNode) throws IllegalArgumentException {
        JsonNode countriesNode = resultsNode.get("country");
        if (countriesNode == null || !countriesNode.isArray()) return List.of();
        return objectMapper.convertValue(countriesNode, new TypeReference<>() {});
    }

    public List<SocialMedia> mapSocialMediaLinks(JsonNode crewNodes) throws Exception {
        List<SocialMedia> socialMediaList = new ArrayList<>();
        if(crewNodes != null && !crewNodes.isEmpty() && crewNodes.isArray()) {
            for (JsonNode crewNode : crewNodes) {
                JsonNode socialMediaNodes = crewNode.path("astronaut").get("social_media_links");
                if (socialMediaNodes != null && !socialMediaNodes.isEmpty() && socialMediaNodes.isArray()) {
                    Integer astronautId = crewNode.path("astronaut").get("id").asInt();
                    for (JsonNode resultNode : socialMediaNodes) {
                        String mediaName = resultNode.path("social_media").get("name").asText();
                        SocialMedia socialMedia = objectMapper.treeToValue(resultNode, SocialMedia.class);
                        socialMedia.setMedia_name(mediaName);
                        socialMedia.setAstronaut_id(astronautId);
                        socialMediaList.add(socialMedia);
                    }
                }
            }
        }
        return socialMediaList;
    }

    public Program mapPrograms(JsonNode programNode, String launchId) throws JsonProcessingException {
        Program program = objectMapper.treeToValue(programNode, Program.class);
        program.setLaunch_id(launchId);
        return program;
    }

    public List<ProgramHasAgencies> mapProgramsHasAgencies(JsonNode programNode) {
        JsonNode agenciesNode = programNode.get("agencies");
        if (agenciesNode == null || !agenciesNode.isArray()) return List.of();
        String programId = programNode.get("id").asText();
        return StreamSupport.stream(agenciesNode.spliterator(), false)
                .map(agencyNode -> {
                    ProgramHasAgencies programHasAgencies = new ProgramHasAgencies();
                    programHasAgencies.setProgram_id(programId);
                    programHasAgencies.setAgency_id(agencyNode.get("id").asText());
                    return programHasAgencies;
                }).toList();
    }

    public List<Updates> mapUpdates(JsonNode resultsNode) {
        JsonNode updatesNode = resultsNode.get("updates");
        if (updatesNode == null || !updatesNode.isArray()) return List.of();
        String id = resultsNode.get("id").asText();
        List<Updates> updates = objectMapper.convertValue(updatesNode, new TypeReference<>() {});
        updates.forEach(update -> update.setLaunch_id(id));
        return updates;
    }

    public List<Video> mapVideos(JsonNode resultsNode) throws IllegalArgumentException {
        JsonNode videoNodes = resultsNode.get("vid_urls");
        if (videoNodes == null || !videoNodes.isArray()) return List.of();
        List<Video> videos = objectMapper.convertValue(videoNodes, new TypeReference<>() {});
        String launchId = resultsNode.get("id").asText();
        videos.forEach(video -> video.setLaunch_id(launchId));
        return videos;
    }

    public List<MissionPatches> mapMissionPatches(JsonNode resultsNode) throws IllegalArgumentException {
        JsonNode patchNodes = resultsNode.get("mission_patches");
        if (patchNodes == null || !patchNodes.isArray()) return List.of();
        String id = resultsNode.get("id").asText();
        List<MissionPatches> missionPatches = objectMapper.convertValue(patchNodes, new TypeReference<>() {});
        missionPatches.forEach(missionPatch -> missionPatch.setLaunch_id(id));
        return missionPatches;
    }

    public List<InfoUrl> mapInfoUrls(JsonNode resultsNode) throws Exception {
        JsonNode infoURLsNode = resultsNode.get("info_urls");
        if (infoURLsNode == null || !infoURLsNode.isArray()) return List.of();
        String id = resultsNode.get("id").asText();
        List<InfoUrl> infoUrls = objectMapper.convertValue(infoURLsNode, new TypeReference<>() {});
        infoUrls.forEach(infoUrl -> infoUrl.setLaunch_id(id));
        return infoUrls;
    }

    public Optional<Image> mapLaunchPadImages(JsonNode resultsNode) throws IOException {
        JsonNode imageNode = resultsNode.path("pad").get("image");
        if (imageNode != null && imageNode.isEmpty()) return Optional.empty();
        Integer id = resultsNode.path("pad").get("id").asInt();
        Image image = objectMapper.treeToValue(imageNode, Image.class);
        image.setLaunch_pad_id(id);
        return Optional.of(image);
    }

    public Optional<Image> mapRocketConfigImages(JsonNode resultsNode) throws IOException {
        JsonNode imageNode = resultsNode.path("rocket").path("configuration").get("image");
        if (imageNode.isEmpty()) return Optional.empty();
        Integer id = resultsNode.path("rocket").path("configuration").get("id").asInt();
        Image image = objectMapper.treeToValue(imageNode, Image.class);
        image.setConfiguration_id(id);
        return Optional.of(image);
    }

    public Optional<Image> mapAgenciesImages(JsonNode resultsNode) throws IOException {
        Integer id = resultsNode.get("id").asInt();
        JsonNode imageNode = resultsNode.get("logo");
        if (imageNode.isEmpty()) return Optional.empty();
        Image image = objectMapper.treeToValue(imageNode, Image.class);
        image.setAgency_id(id);
        return Optional.of(image);
    }

    public Optional<Image> mapAstronautImages(JsonNode crewNodes) throws IOException {
        for (JsonNode crewNode : crewNodes){
            JsonNode imageNode = crewNode.path("astronaut").get("image");
            if (imageNode != null && !imageNode.isEmpty()){
                Integer id =  crewNode.path("astronaut").get("id").asInt();
                Image image = objectMapper.treeToValue(imageNode, Image.class);
                image.setAstronaut_id(id);
                return Optional.of(image);
            }
        }
        return Optional.empty();
    }

    public List<Image> mapLauncherImages(JsonNode resultsNode) throws IOException {
        List<Image> images = new ArrayList<>();
        JsonNode launcherStageNodes = resultsNode.path("rocket").get("launcher_stage");
        if (launcherStageNodes != null && !launcherStageNodes.isEmpty() && launcherStageNodes.isArray()){
            for (JsonNode stage : launcherStageNodes) {
                JsonNode imageNode = stage.path("launcher").get("image");
                if (imageNode != null && !imageNode.isEmpty()) {
                    Integer id = stage.path("launcher").get("id").asInt();
                    Image image = objectMapper.treeToValue(imageNode, Image.class);
                    image.setLauncher_id(id);
                    images.add(image);
                }
            }
        }
        return images;
    }

    public Optional<Image> mapSpacecraftConfigImages(JsonNode resultsNode) throws IOException {
        JsonNode spacecraftStageNodes = resultsNode.path("rocket").get("spacecraft_stage");
        if (spacecraftStageNodes.isEmpty() && !spacecraftStageNodes.isArray()) {
            return Optional.empty();
        }

        for (JsonNode stage : spacecraftStageNodes) {
            Integer id = stage.path("spacecraft").path("spacecraft_config").get("id").asInt();
            JsonNode imageNode = stage.path("spacecraft").path("spacecraft_config").get("image");
            if (imageNode != null && !imageNode.isEmpty()) {
                Image image = objectMapper.treeToValue(imageNode, Image.class);
                image.setSpacecraft_id(id);
                return Optional.of(image);
            }
        }
        return Optional.empty();
    }
}
