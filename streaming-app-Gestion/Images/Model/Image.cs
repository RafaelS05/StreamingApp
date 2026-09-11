namespace streaming_app_Gestion.Image.Model
{
    public class Image
    {
        private int IdImage { get; set; }
        private string ImageName { get; set; } = string.Empty;
        private string firebaseUrl { get; set; } = string.Empty;
        private DateTime ImageDate { get; set; } = DateTime.UtcNow;
    }
}
