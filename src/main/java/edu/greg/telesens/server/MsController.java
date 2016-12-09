package edu.greg.telesens.server;

import edu.greg.telesens.server.commands.PlayCmd;
import edu.greg.telesens.server.services.PlayerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by
 * GreG on 11/8/2016.
 */
@Slf4j
@RestController
public class MsController {

    private final PlayerService playerService;

    @Autowired
    public MsController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @RequestMapping("/play")
    public HttpEntity<PlayCmd> greeting(
            @RequestParam(value = "host", required = false, defaultValue = "localhost") String host,
            @RequestParam(value = "port", required = false, defaultValue = "1314") String port,
            @RequestParam(value = "melodyPath", required = false, defaultValue = "file:///P:/nrt10/content/nrt-content-root/ip/699/00/02/699000263.vox") String melodyPath,
            @RequestParam(value = "codec", required = false, defaultValue = "TestCodeck") String codec,
            @RequestParam(value = "rep", required = false, defaultValue = "1") String repeat) {
        log.info("Process play command for {}:{}:{}:{}", host, port, codec, repeat);

        playerService.play(host, Integer.parseInt(port), melodyPath, codec, Integer.parseInt(repeat));

        PlayCmd playCmd = new PlayCmd(String.format(host, port, melodyPath, codec, repeat));
        playCmd.add(linkTo(methodOn(MsController.class).greeting(host, port, melodyPath, codec, repeat)).withSelfRel());

        return new ResponseEntity<PlayCmd>(playCmd, HttpStatus.OK);
    }

    @RequestMapping("/stop")
    public HttpEntity<PlayCmd> greeting(
            @RequestParam(value = "callerAddress", required = false, defaultValue = "13131313") String msidn) {
        log.info("Process stop command for {}:{}:{}.", msidn);

        playerService.stop();

        PlayCmd playCmd = new PlayCmd(msidn);
        playCmd.add(linkTo(methodOn(MsController.class).greeting(msidn)).withSelfRel());

        return new ResponseEntity<PlayCmd>(playCmd, HttpStatus.OK);
    }
}
