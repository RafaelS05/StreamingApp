using streaming_app_Gestion.Categories.DTO;
namespace streaming_app_Gestion.Categories.Service
{
    public interface ICategoryService
    {
        Task<List<CategoryResponseDto>> GetAllCategoriesAsync(CancellationToken ct = default);
        Task<CategoryResponseDto?> GetCategoryByIdAsync(int id, CancellationToken ct = default);
        Task<CategoryResponseDto> CreateCategoryAsync(CategoryCreateDto categoryCreateDto, CancellationToken ct = default);
        Task<CategoryResponseDto?> UpdateCategoryAsync(int id, CategoryUpdateDto categoryUpdateDto, CancellationToken ct = default);
        Task<bool> DeleteCategoryAsync(int id, CancellationToken ct = default);
    }
}
