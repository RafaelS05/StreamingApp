namespace streaming_app_Gestion.ErrorHandler.Model
{
    public class Error
    {
        private int IdError { get; set; }
        private string ErrorMessage { get; set; } = string.Empty;
        private DateTime ErrorDate { get; set; } = DateTime.UtcNow;
    }
}
