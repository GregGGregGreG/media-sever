package edu.greg.telesens.server;

/**
 * Created by
 * GreG on 11/8/2016.
 */
//@Slf4j
//@RestController
public class MsController {

//    private final PlayerService playerService;
//
//    @Autowired
//    public MsController(PlayerService playerService) {
//        this.playerService = playerService;
//    }
//

//    @Autowired
//    private SessionRegistry sessionRegistry;
//
//    private ClientSession session;
//
//    private AudioFormat g711a = FormatFactory.createAudioFormat("pcma", 8000, 8, 1);
//
//
//    @RequestMapping("/play")
//    public HttpEntity<PlayCmd> greeting(
//            @RequestParam(value = "host", required = false, defaultValue = "localhost") String host,
//            @RequestParam(value = "port", required = false, defaultValue = "1314") String port,
//            @RequestParam(value = "melodyPath", required = false, defaultValue = "file:///P:/nrt10/content/nrt-content-root/ip/699/00/02/699000263.vox") String melodyPath,
//            @RequestParam(value = "codec", required = false, defaultValue = "TestCodeck") String codec,
//            @RequestParam(value = "rep", required = false, defaultValue = "1") String repeat) {
//        log.info("Process play command for {}:{}:{}:{}", host, port, codec, repeat);
//
//        try {
//            ClientSession session = sessionRegistry.register("streamer", host, Integer.parseInt(port), melodyPath, g711a, Integer.parseInt(repeat));
//            log.info("initialized session " + session.getSessionId());
//            sessionRegistry.play(session.getSessionId());
//            this.session = session;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
////        playerService.play(host, Integer.parseInt(port), melodyPath, codec, Integer.parseInt(repeat));
//
//        PlayCmd playCmd = new PlayCmd(String.format(host, port, melodyPath, codec, repeat, session.getSessionId()));
//        playCmd.add(linkTo(methodOn(MsController.class).greeting(host, port, melodyPath, codec, repeat)).withSelfRel());
//
//        return new ResponseEntity<PlayCmd>(playCmd, HttpStatus.OK);
//    }
//
//    @RequestMapping("/stop")
//    public HttpEntity<PlayCmd> greeting(
//            @RequestParam(value = "callerAddress", required = false, defaultValue = "13131313") String msidn) {
//        log.info("Process stop command for {}:{}:{}.", msidn);
//
//        sessionRegistry.stop(session.getSessionId());
//
////        playerService.stop();
//
//        PlayCmd playCmd = new PlayCmd(msidn);
//        playCmd.add(linkTo(methodOn(MsController.class).greeting(msidn)).withSelfRel());
//
//        return new ResponseEntity<PlayCmd>(playCmd, HttpStatus.OK);
//    }
}
