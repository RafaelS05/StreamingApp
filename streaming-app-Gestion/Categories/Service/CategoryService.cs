using streaming_app_Gestion.Categories.Model;
using streaming_app_Gestion.Categories.DTO;
using streaming_app_Gestion.Categories.Repository;


namespace streaming_app_Gestion.Categories.Service
{
    public class CategoryService : ICategoryService
    {
        private readonly ICategoryRepository _categoryRepository;
        private readonly AppDbContext _context;
        public CategoryService(ICategoryRepository categoryRepository, AppDbContext context)
        {
            _categoryRepository = categoryRepository;
            _context = context;
        }
        public async Task<List<CategoryResponseDto>> GetAllCategoriesAsync(CancellationToken ct = default)
        {
            var items = await _categoryRepository.GetAllCategoriesAsync(ct);
            return items.Select(ToResponse).ToList();
        }


        public async Task<CategoryResponseDto?> GetCategoryByIdAsync(int id, CancellationToken ct = default)
        {
            var item = await _categoryRepository.GetCategoryByIdAsync(id, ct);
            return item is null ? null : ToResponse(item);
        }

        public async Task<CategoryResponseDto> CreateCategoryAsync(CategoryCreateDto categoryCreateDto, CancellationToken ct = default)
        {
            var category = new Category
            {
                nameCategory = categoryCreateDto.Name,
                descriptionCategory = categoryCreateDto.Description
            };
            await _categoryRepository.AddCategoryAsync(category, ct);
            await _context.SaveChangesAsync(ct);

            return ToResponse(category);
        }

        public async Task<CategoryResponseDto?> UpdateCategoryAsync(int id, CategoryUpdateDto categoryUpdateDto, CancellationToken ct = default)
        {
            var category = await _categoryRepository.GetCategoryByIdAsync(id, ct);
            if (category is null)
            {
                return null;
            }
            category.nameCategory = categoryUpdateDto.Name;
            category.descriptionCategory = categoryUpdateDto.Description;
            await _categoryRepository.UpdateCategoryAsync(category, ct);
            await _context.SaveChangesAsync(ct);
            return ToResponse(category);
        }

        public async Task<bool> DeleteCategoryAsync(int id, CancellationToken ct = default)
        {
            var category = await _categoryRepository.GetCategoryByIdAsync(id, ct);
            if (category is null)
            {
                return false;
            }
            await _categoryRepository.DeleteCategoryAsync(category, ct);
            await _context.SaveChangesAsync(ct);
            return true;
        }

        private static CategoryResponseDto ToResponse(Category category)
        {
            return new CategoryResponseDto
            {
                IdCategory = category.IdCategory,
                Name = category.nameCategory,
                Description = category.descriptionCategory
            };
        }
    }
