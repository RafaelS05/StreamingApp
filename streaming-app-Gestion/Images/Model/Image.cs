namespace streaming_app_Gestion.Image.Model
{
    public class Image
    {
        public int IdImage { get; set; }
        public IFormFile ImageFile { get; set; } = null!;
        public string firebaseUrl { get; set; } = string.Empty;
        public DateTime ImageDate { get; set; } = DateTime.UtcNow;
    }
}
