package com.moonkeyeu.etl.api.dto.chunks;

import com.moonkeyeu.etl.api.configuration.files.CsvSource;
import com.moonkeyeu.etl.api.dto.Images.Image;
import com.moonkeyeu.etl.api.dto.agency.Agency;
import com.moonkeyeu.etl.api.dto.country.AgencyHasCountry;
import com.moonkeyeu.etl.api.dto.country.AstronautHasNationalities;
import com.moonkeyeu.etl.api.dto.country.Country;
import com.moonkeyeu.etl.api.dto.crew.LaunchCrew;
import com.moonkeyeu.etl.api.dto.launch.Launch;
import com.moonkeyeu.etl.api.dto.launcher.LauncherStage;
import com.moonkeyeu.etl.api.dto.media.*;
import com.moonkeyeu.etl.api.dto.program.Program;
import com.moonkeyeu.etl.api.dto.program.ProgramHasAgencies;
import com.moonkeyeu.etl.api.dto.spacecraft.SpaceCraftStage;
import lombok.Data;
import lombok.Getter;
import org.springframework.batch.infrastructure.item.Chunk;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
public class ChunkStore {
    private Chunk<Agency> agencies = new Chunk<>();
    private Chunk<Launch> launches = new Chunk<>();
    private Chunk<Image> padImages = new Chunk<>();
    private Chunk<Image> agenciesImages = new Chunk<>();
    private Chunk<Image> rocketConfImages = new Chunk<>();
    private Chunk<Image> spacecraftConfImages = new Chunk<>();
    private Chunk<Image> launcherImages = new Chunk<>();
    private Chunk<Image> astronautImages = new Chunk<>();
    private Chunk<LauncherStage> launcherStage = new Chunk<>();
    private Chunk<SpaceCraftStage> spacecraftStage = new Chunk<>();
    private Chunk<LaunchCrew> crewList = new Chunk<>();
    private Chunk<SocialMedia> socialMedia = new Chunk<>();
    private Chunk<AstronautHasNationalities> nationalities = new Chunk<>();
    private Chunk<Country> countries = new Chunk<>();
    private Chunk<Video> videoList = new Chunk<>();
    private Chunk<Updates> updates = new Chunk<>();
    private Chunk<Program> programs = new Chunk<>();
    private Chunk<ProgramHasAgencies> programHasAgencies = new Chunk<>();
    private Chunk<MissionPatches> patches = new Chunk<>();
    private Chunk<InfoUrl> infoUrls = new Chunk<>();
    private Chunk<AgencyHasCountry> agencyHasCountries = new Chunk<>();

    private final List<ChunkMapper<?>> tasks = new ArrayList<>();

    public void add(Chunk<?> data, CsvSource target) {
        tasks.add(new ChunkMapper<>(data, target));
    }
}
