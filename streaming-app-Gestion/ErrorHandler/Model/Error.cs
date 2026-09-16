namespace streaming_app_Gestion.ErrorHandler.Model
{
    public class Error
    {
        public int IdError { get; set; }
        public string ErrorMessage { get; set; } = string.Empty;
        public DateTime ErrorDate { get; set; } = DateTime.UtcNow;
    }
}
