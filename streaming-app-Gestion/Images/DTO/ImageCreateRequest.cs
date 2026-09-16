using System.ComponentModel.DataAnnotations;

namespace streaming_app_Gestion.Image.DTO
{
    public class ImageCreateRequest
    {
        [Required]
        public IFormFile ImageFile { get; set; } = null!;
    }
}
