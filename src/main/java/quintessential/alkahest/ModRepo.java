package quintessential.alkahest;

public record ModRepo(
	String name, String owner
){
	
	public String rawUrl(){
		return "https://raw.githubusercontent.com/" + owner + "/" + name;
	}
}