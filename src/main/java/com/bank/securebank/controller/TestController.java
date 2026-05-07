@RestController
public class TestController {

    @GetMapping("/")
    public String home() {
        return "Backend Running Successfully!";
    }
}